package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
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
import faang.school.projectservice.validator.UpdateVacancyRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.lang.Nullable;

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
    private ProjectServiceImpl projectService;
    @Mock
    private TeamMemberServiceImpl teamMemberService;
    @Mock
    private CandidateServiceImpl candidateService;
    @Mock
    private OpenVacancyRequestValidator openVacancyRequestValidator;
    @Mock
    private UpdateVacancyRequestValidator updateVacancyRequestValidator;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);
    @Spy
    private CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);

    @Mock
    private VacancyFilter vacancyFilter1;

    @Mock
    private VacancyFilter vacancyFilter2;

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    @BeforeEach
    public void setUp() {
        vacancyService = new VacancyServiceImpl(
                vacancyRepository,
                projectService,
                teamMemberService,
                candidateService,
                openVacancyRequestValidator,
                updateVacancyRequestValidator,
                vacancyMapper,
                candidateMapper,
                List.of(vacancyFilter1, vacancyFilter2)
        );
    }

    @Test
    public void testOpenVacancy_FailedProjectValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(0, 1, null);
        when(openVacancyRequestValidator.validateAndGetProject(requestDto))
                .thenThrow(new DataValidationException("Invalid project"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid project");
    }

    @Test
    public void testOpenVacancy_FailedAuthorValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(1, 0, null);
        when(openVacancyRequestValidator.validateAndGetProject(requestDto)).thenReturn(new Project());
        when(openVacancyRequestValidator.validateAndGetAuthor(requestDto))
                .thenThrow(new DataValidationException("Invalid author"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid author");
    }

    @Test
    public void testOpenVacancy_FailedSalaryValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(1, 0, null);
        when(openVacancyRequestValidator.validateAndGetProject(requestDto)).thenReturn(new Project());
        when(openVacancyRequestValidator.validateAndGetAuthor(requestDto)).thenReturn(new TeamMember());
        Mockito.doThrow(new DataValidationException("Invalid salary")).
                when(openVacancyRequestValidator)
                .validateSalary(requestDto);

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid salary");
    }

    @Test
    public void testOpenVacancy_ValidData_SaveVacancy() {
        // Arrange
        var projectId = 1L;
        var authorId = 2L;
        var requestDto = createOpenVacancyRequestDto(projectId, authorId, 10.5);

        var project = Project.builder()
                .id(projectId)
                .name("Test project")
                .build();
        when(openVacancyRequestValidator.validateAndGetProject(requestDto)).thenReturn(project);

        var author = TeamMember.builder()
                .id(authorId)
                .nickname("Test author")
                .build();
        when(openVacancyRequestValidator.validateAndGetAuthor(requestDto)).thenReturn(author);

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
    public void testUpdateVacancy_FailedVacancyValidation_Throws() {
        var requestDto = createUpdateVacancyRequestDto(0L, 1L, null, null);
        when(updateVacancyRequestValidator.validateAndGetVacancy(requestDto))
                .thenThrow(new DataValidationException("Invalid vacancy"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.updateVacancy(requestDto),
                "Invalid vacancy");
    }

    @Test
    public void testUpdateVacancy_FailedUpdaterRoleValidation_Throws() {
        var requestDto = createUpdateVacancyRequestDto(0L, 1L, null, null);
        when(updateVacancyRequestValidator.validateAndGetVacancy(requestDto)).thenReturn(new Vacancy());
        Mockito.doThrow(new DataValidationException("Invalid updater role")).
                when(updateVacancyRequestValidator)
                .validateUpdaterRole(requestDto);

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.updateVacancy(requestDto),
                "Invalid updater role");
    }

    @Test
    public void testUpdateVacancy_FailedCandidatesCountValidation_Throws() {
        // Arrange
        var vacancyId = 1L;
        var projectId = 1L;
        var vacancy = Vacancy.builder()
                .id(vacancyId)
                .project(getTestProject(projectId))
                .build();
        List<Candidate> attachedToProjectCandidates = List.of();

        var requestDto = createUpdateVacancyRequestDto(vacancyId, 1L, null, null);

        when(updateVacancyRequestValidator.validateAndGetVacancy(requestDto)).thenReturn(vacancy);
        when(candidateService.getAllCandidatesAttachedToProjectVacancy(vacancyId, projectId))
                .thenReturn(attachedToProjectCandidates);
        Mockito.doThrow(new DataValidationException("Invalid candidates count")).
                when(updateVacancyRequestValidator)
                .validateCandidatesCount(requestDto, vacancy, attachedToProjectCandidates);

        // Act + Assert
        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.updateVacancy(requestDto),
                "Invalid candidates count");
    }

    @Test
    public void testUpdateVacancy_ValidData_ReturnVacancy() {
        // Arrange
        var vacancyId = 1L;
        var updaterId = 2L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, updaterId, VacancyStatus.CLOSED, 10);

        var projectId = 1L;
        var project = getTestProject(projectId);
        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(
                Optional.of(project));

        var authorId = 3L;
        var author = getTestAuthor(authorId);
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(
                Optional.of(author));

        var updater = getTestUpdater(updaterId);
        when(teamMemberService.getTeamMemberById(updaterId)).thenReturn(
                Optional.of(updater));

        var vacancy = Vacancy.builder()
                .id(vacancyId)
                .project(project)
                .createdBy(authorId)
                .updatedBy(updaterId)
                .build();
        when(updateVacancyRequestValidator.validateAndGetVacancy(requestDto)).thenReturn(vacancy);

        var candidate = new Candidate();
        candidate.setId(2L);
        candidate.setUsername("Test user name");
        candidate.setVacancy(vacancy);
        List<Candidate> attachedToProjectCandidates = List.of(candidate);
        when(candidateService.getAllCandidatesAttachedToProjectVacancy(vacancyId, projectId))
                .thenReturn(attachedToProjectCandidates);

        // Act
        vacancyService.updateVacancy(requestDto);

        // Assert
        verify(vacancyRepository, times(1)).save(vacancyCaptor.capture());
        var savedVacancy = vacancyCaptor.getValue();
        assertEquals(requestDto.name(), savedVacancy.getName());
        assertEquals(requestDto.description(), savedVacancy.getDescription());
        assertEquals(requestDto.status(), savedVacancy.getStatus());
        assertEquals(requestDto.position(), savedVacancy.getPosition());
        assertEquals(CandidateStatus.ACCEPTED, candidate.getCandidateStatus());
    }

    @Test
    public void testUpdateVacancy_ValidDataWithoutChanges_ReturnVacancy() {
        // Arrange
        var vacancyId = 1L;
        var updaterId = 2L;
        var requestDto = new UpdateVacancyRequestDto(
                vacancyId,
                updaterId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        var projectId = 1L;
        var project = getTestProject(projectId);
        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(
                Optional.of(project));

        var authorId = 3L;
        var author = getTestAuthor(authorId);
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(
                Optional.of(author));

        var updater = getTestUpdater(updaterId);
        when(teamMemberService.getTeamMemberById(updaterId)).thenReturn(
                Optional.of(updater));

        var vacancyName = "Test vacancy";
        var vacancyDescription = "Test description";
        var vacancyPosition = TeamRole.DEVELOPER;
        var vacancyStatus = VacancyStatus.OPEN;
        var vacancy = Vacancy.builder()
                .id(vacancyId)
                .name(vacancyName)
                .description(vacancyDescription)
                .position(vacancyPosition)
                .status(vacancyStatus)
                .project(project)
                .createdBy(authorId)
                .updatedBy(updaterId)
                .build();
        when(updateVacancyRequestValidator.validateAndGetVacancy(requestDto)).thenReturn(vacancy);

        var candidate = new Candidate();
        candidate.setId(2L);
        candidate.setUsername("Test user name");
        candidate.setVacancy(vacancy);
        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
        List<Candidate> attachedToProjectCandidates = List.of(candidate);
        when(candidateService.getAllCandidatesAttachedToProjectVacancy(vacancyId, projectId))
                .thenReturn(attachedToProjectCandidates);

        // Act
        vacancyService.updateVacancy(requestDto);

        // Assert
        verify(vacancyRepository, times(1)).save(vacancyCaptor.capture());
        var savedVacancy = vacancyCaptor.getValue();
        assertEquals(vacancyName, savedVacancy.getName());
        assertEquals(vacancyDescription, savedVacancy.getDescription());
        assertEquals(vacancyStatus, savedVacancy.getStatus());
        assertEquals(vacancyPosition, savedVacancy.getPosition());
        assertEquals(CandidateStatus.WAITING_RESPONSE, candidate.getCandidateStatus());
    }

    @Test
    public void testGetFilteredVacancies_AllFiltersAreNotApplicable_ReturnsOriginalRequests() {
        // Arrange
        var filterDto = new FilterVacancyRequestDto(null, null);

        var project = getTestProject(1L);
        var authorId = 3L;
        var author = getTestAuthor(authorId);
        var vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("Java Developer")
                        .status(VacancyStatus.OPEN)
                        .project(project)
                        .createdBy(authorId)
                        .build(),
                Vacancy.builder()
                        .id(2L)
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
                expectedResult.stream().map(VacancyResponseDto::getId).toList(),
                result.stream().map(VacancyResponseDto::getId).toList());
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
        var filterDto = new FilterVacancyRequestDto(null, "Test");

        var vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("Java Developer")
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("Kotlin Developer")
                        .build());
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
        var filterDto = new FilterVacancyRequestDto(positionToSearch, namePatternToSearch);

        var project = getTestProject(1L);
        var authorId = 3L;
        var author = getTestAuthor(authorId);

        var vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("JavaScript")
                        .position(TeamRole.DEVELOPER)
                        .project(project)
                        .createdBy(authorId)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("Java")
                        .position(TeamRole.DEVELOPER)
                        .project(project)
                        .createdBy(authorId)
                        .build(),
                Vacancy.builder()
                        .id(3L)
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
                expectedResult.stream().map(VacancyResponseDto::getId).toList(),
                result.stream().map(VacancyResponseDto::getId).toList());
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
        var project = getTestProject(1L);
        var authorId = 3L;
        var author = getTestAuthor(authorId);
        var vacancyId = 1L;
        var vacancy = Vacancy.builder()
                .id(vacancyId)
                .name("Test name")
                .candidates(new ArrayList<>())
                .project(project)
                .createdBy(authorId)
                .build();

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
        assertEquals(vacancy.getId(), result.getId());
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

    private static UpdateVacancyRequestDto createUpdateVacancyRequestDto(
            long vacancyId,
            long updaterId,
            @Nullable VacancyStatus status,
            @Nullable Integer requiredCandidatesCount) {
        return new UpdateVacancyRequestDto(
                vacancyId,
                updaterId,
                "Test name",
                "Test description",
                TeamRole.ANALYST,
                status,
                requiredCandidatesCount,
                null,
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

    private static TeamMember getTestUpdater(long updaterId) {
        return TeamMember.builder()
                .id(updaterId)
                .nickname("Updater nickname")
                .build();
    }

    private void setupVacancyFilter(
            VacancyFilter filter,
            FilterVacancyRequestDto filterDto,
            boolean isApplicable,
            Answer<Stream<Vacancy>> filterApplyAnswer) {
        when(filter.isApplicable(filterDto)).thenReturn(isApplicable);
        when(filter.apply(any(), any())).thenAnswer(filterApplyAnswer);
    }
}