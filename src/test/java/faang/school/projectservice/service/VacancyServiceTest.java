package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.vacancy.AdjustableVacancyAnswer;
import faang.school.projectservice.filter.vacancy.ReturnEmptyStreamVacancyAnswer;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.CandidateMapper;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.OpenVacancyRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private OpenVacancyRequestValidator openVacancyRequestValidator;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);
    @Spy
    private CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);
    ;

    @Mock
    private VacancyFilter vacancyFilter1;

    @Mock
    private VacancyFilter vacancyFilter2;

    @InjectMocks
    private VacancyService vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    @BeforeEach
    public void setUp() {
        vacancyService = new VacancyService(
                vacancyRepository,
                projectService,
                teamMemberService,
                openVacancyRequestValidator,
                vacancyMapper,
                candidateMapper,
                List.of(vacancyFilter1, vacancyFilter2)
        );
    }

    @Test
    public void testOpenVacancy_FailedProjectValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(0, 1, null);
        when(openVacancyRequestValidator.validateProject(requestDto))
                .thenThrow(new DataValidationException("Invalid project"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid project");
    }

    @Test
    public void testOpenVacancy_FailedAuthorValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(1, 0, null);
        when(openVacancyRequestValidator.validateProject(requestDto)).thenReturn(new Project());
        when(openVacancyRequestValidator.validateAuthor(requestDto))
                .thenThrow(new DataValidationException("Invalid author"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid author");
    }

    @Test
    public void testOpenVacancy_FailedSalaryValidation_Throws() {
        // Arrange
        var projectId = 1L;
        var authorId = 2L;
        var requestDto = createOpenVacancyRequestDto(projectId, authorId, 10.5);

        var project = Project.builder()
                .id(projectId)
                .name("Test project")
                .build();
        when(openVacancyRequestValidator.validateProject(requestDto)).thenReturn(project);

        var author = TeamMember.builder()
                .id(authorId)
                .nickname("Test author")
                .build();
        when(openVacancyRequestValidator.validateAuthor(requestDto)).thenReturn(author);

        // Act
        vacancyService.openVacancy(requestDto);

        // Assert
        verify(vacancyRepository, times(1)).save(vacancyCaptor.capture());
        var savedVacancy = vacancyCaptor.getValue();
        assertEquals(VacancyStatus.OPEN, savedVacancy.getStatus());
        assertEquals(authorId, savedVacancy.getCreatedBy());
        assertEquals(projectId, savedVacancy.getProject().getId());
        assertEquals(project.getName(), savedVacancy.getProject().getName());
    }

    @Test
    public void testGetFilteredVacancies_AllFiltersAreNotApplicable_ReturnsOriginalRequests() {
        // Arrange
        var filterDto = new VacancyFilterRequestDto();

        var project = getTestProject(1L);
        var authorId = 3L;
        var author = getTestAuthor(authorId);
        var vacancies = List.of(
                Vacancy.builder()
                        .name("Java Developer")
                        .status(VacancyStatus.OPEN)
                        .project(project)
                        .createdBy(authorId)
                        .build(),
                Vacancy.builder()
                        .name("Kotlin Developer")
                        .status(VacancyStatus.CLOSED)
                        .project(project)
                        .createdBy(authorId)
                        .build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);

        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(
                Optional.of(project));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(
                Optional.of(author));

        when(candidateMapper.ToCandidateDtos(any())).thenReturn(List.of());

        when(vacancyFilter1.isApplicable(filterDto)).thenReturn(false);
        when(vacancyFilter2.isApplicable(filterDto)).thenReturn(false);

        // Act
        var result = vacancyService.getFilteredVacancies(filterDto);

        // Assert
        var expectedResult = vacancyMapper.ToVacancyResponseDtos(vacancies);
        assertIterableEquals(
                expectedResult.stream().map(VacancyResponseDto::getName).toList(),
                result.stream().map(VacancyResponseDto::getName).toList());
        assertIterableEquals(
                expectedResult.stream().map(VacancyResponseDto::getStatus).toList(),
                result.stream().map(VacancyResponseDto::getStatus).toList());
    }

    @Test
    public void testGetFilteredVacancies_AllRequestsAreNotMatched_ReturnsEmptyList() {
        // Arrange
        var filterDto = new VacancyFilterRequestDto();
        filterDto.setNamePattern("Test");

        var vacancies = List.of(
                Vacancy.builder().name("Java Developer").build(),
                Vacancy.builder().name("Kotlin Developer").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);

        setupVacancyFilter(vacancyFilter1, filterDto, true, new ReturnEmptyStreamVacancyAnswer());
        setupVacancyFilter(vacancyFilter2, filterDto, true, new ReturnEmptyStreamVacancyAnswer());

        // Act
        var result = vacancyService.getFilteredVacancies(filterDto);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFilteredVacancies_SomeRequestsAreMatched_ReturnsNotEmptyList() {
        // Arrange
        var namePatternToSearch = "Java";
        var positionToSearch = TeamRole.DEVELOPER;
        var filterDto = new VacancyFilterRequestDto();
        filterDto.setNamePattern(namePatternToSearch);
        filterDto.setPosition(positionToSearch);

        var project = getTestProject(1L);
        var authorId = 3L;
        var author = getTestAuthor(authorId);

        var vacancies = List.of(
                Vacancy.builder()
                        .name("JavaScript")
                        .position(TeamRole.DEVELOPER)
                        .project(project)
                        .createdBy(authorId)
                        .build(),
                Vacancy.builder()
                        .name("Java")
                        .position(TeamRole.DEVELOPER)
                        .project(project)
                        .createdBy(authorId)
                        .build(),
                Vacancy.builder()
                        .name("Python")
                        .position(TeamRole.DEVELOPER)
                        .project(project)
                        .createdBy(authorId)
                        .build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);

        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(
                Optional.of(project));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(
                Optional.of(author));

        setupVacancyFilter(
                vacancyFilter1,
                filterDto,
                true,
                new AdjustableVacancyAnswer(vacancy -> vacancy.getName().contains(namePatternToSearch)));
        setupVacancyFilter(
                vacancyFilter2,
                filterDto,
                true,
                new AdjustableVacancyAnswer(vacancy -> vacancy.getPosition() == positionToSearch));

        List<VacancyResponseDto> expectedResult = new ArrayList<>();
        expectedResult.add(vacancyMapper.ToVacancyResponseDto(vacancies.get(0)));
        expectedResult.add(vacancyMapper.ToVacancyResponseDto(vacancies.get(1)));

        // Act
        var result = vacancyService.getFilteredVacancies(filterDto);

        // Assert
        assertIterableEquals(
                expectedResult.stream().map(VacancyResponseDto::getName).toList(),
                result.stream().map(VacancyResponseDto::getName).toList());
        assertIterableEquals(
                expectedResult.stream().map(VacancyResponseDto::getPosition).toList(),
                result.stream().map(VacancyResponseDto::getPosition).toList());
    }

    @Test
    public void testGetVacancyById_RequestNotFound_Throws() {
        var vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        var result = vacancyService.getVacancyById(vacancyId);

        assertNull(result);
    }

    @Test
    public void testGetVacancyById_RequestFound_ReturnsVacancy() {
        // Arrange
        var vacancyId = 1L;
        var vacancy = Vacancy.builder()
                .id(vacancyId)
                .name("Test name")
                .candidates(new ArrayList<>())
                .build();

        var project = getTestProject(1L);
        vacancy.setProject(project);

        var authorId = 3L;
        var author = getTestAuthor(authorId);
        vacancy.setCreatedBy(authorId);

        var candidate = new Candidate();
        candidate.setId(4L);
        candidate.setUsername("Candidate nickname");
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        var candidates = List.of(candidate);
        vacancy.getCandidates().addAll(candidates);

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(
                Optional.of(project));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(
                Optional.of(author));

        // Act
        var result = vacancyService.getVacancyById(vacancyId);

        // Assert
        assertNotNull(result);
        assertEquals(vacancy.getName(), result.getName());
        assertEquals(vacancy.getProject().getName(), result.getProjectName());
        assertEquals(author.getNickname(), result.getCreatedByNickname());
        assertNotNull(result.getCandidates());
        assertEquals(candidates.size(), result.getCandidates().size());
        assertEquals(candidates.get(0).getUsername(), result.getCandidates().get(0).username());
        assertEquals(candidates.get(0).getCandidateStatus(), result.getCandidates().get(0).candidateStatus());
        verify(projectService, times(1))
                .getProjectByIdOrEmpty(project.getId());
        verify(teamMemberService, times(1))
                .getTeamMemberById(authorId);
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(long projectId, long authorId, Double salary) {
        return new OpenVacancyRequestDto(
                "Test name",
                "Test description",
                projectId,
                TeamRole.ANALYST,
                1,
                authorId,
                salary,
                null,
                null);
    }

    private static Project getTestProject(long projectId) {
        return Project.builder().id(projectId).build();
    }

    private static TeamMember getTestAuthor(long authorId) {
        return TeamMember.builder()
                .id(authorId)
                .nickname("Author nickname")
                .build();
    }

    private void setupVacancyFilter(
            VacancyFilter filter,
            VacancyFilterRequestDto filterDto,
            boolean isApplicable,
            Answer<Stream<Vacancy>> filterApplyAnswer) {
        when(filter.isApplicable(filterDto)).thenReturn(isApplicable);
        when(filter.apply(any(), any())).thenAnswer(filterApplyAnswer);
    }
}