package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.RecordNotFoundException;
import faang.school.projectservice.exception.ResourceForbiddenException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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

    @Mock
    private UserContext userContext;

    @Mock
    private S3ServiceImpl s3Service;

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    @Captor
    private ArgumentCaptor<String> coverImageKeyCaptor;

    @BeforeEach
    public void setUp() {
        vacancyService = new VacancyServiceImpl(s3Service, vacancyRepository, projectService, teamMemberService, candidateService,
                openVacancyRequestValidator, updateVacancyRequestValidator, vacancyMapper, candidateMapper,
                List.of(vacancyFilter1, vacancyFilter2), userContext);
        ReflectionTestUtils.setField(vacancyService, "limitSize", 5);
        ReflectionTestUtils.setField(vacancyService, "limitSide", 512);
    }

    @Test
    public void shouldOpenVacancy_throw_whenProjectIdIsNotPresented() {
        var projectId = 0;
        var requestDto = createOpenVacancyRequestDto(projectId, 1, null);
        when(projectService.getProjectByIdOrEmpty(projectId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> vacancyService.openVacancy(requestDto));
    }

    @Test
    public void shouldVOpenVacancy_throw_whenAuthorIdIsNotPresented() {
        var authorId = 0;
        var requestDto = createOpenVacancyRequestDto(1, authorId, null);
        when(projectService.getProjectByIdOrEmpty(requestDto.projectId())).thenReturn(Optional.of(new Project()));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> vacancyService.openVacancy(requestDto));
    }

    @Test
    public void shouldOpenVacancy_throw_whenAuthorValidationIsFailed() {
        var requestDto = createOpenVacancyRequestDto(1, 0, null);
        when(projectService.getProjectByIdOrEmpty(requestDto.projectId())).thenReturn(Optional.of(new Project()));
        when(teamMemberService.getTeamMemberById(requestDto.authorId()))
                .thenThrow(new DataValidationException("Invalid author"));

        assertThrowsExactly(DataValidationException.class, () -> vacancyService.openVacancy(requestDto),
                "Invalid author");
    }

    @Test
    public void shouldOpenVacancy_throw_whenSalaryValidationIsFailed() {
        var requestDto = createOpenVacancyRequestDto(1, 0, null);
        when(projectService.getProjectByIdOrEmpty(requestDto.projectId())).thenReturn(Optional.of(new Project()));
        when(teamMemberService.getTeamMemberById(requestDto.authorId())).thenReturn(Optional.of(new TeamMember()));
        Mockito.doThrow(new DataValidationException("Invalid salary"))
                .when(openVacancyRequestValidator)
                .validateSalary(requestDto);

        assertThrowsExactly(DataValidationException.class, () -> vacancyService.openVacancy(requestDto),
                "Invalid salary");
    }

    @Test
    public void shouldOpenVacancy_saveVacancy_whenDataIsValid() {
        // Arrange
        var projectId = 1L;
        var authorId = 2L;
        var requestDto = createOpenVacancyRequestDto(projectId, authorId, 10.5);

        var project = Project.builder().id(projectId).name("Test project").build();
        when(projectService.getProjectByIdOrEmpty(requestDto.projectId())).thenReturn(Optional.of(project));

        var author = TeamMember.builder().id(authorId).nickname("Test author").build();
        when(teamMemberService.getTeamMemberById(requestDto.authorId())).thenReturn(Optional.of(author));

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
    public void shouldUpdateVacancy_throw_whenVacancyIdIsNotPresented() {
        var vacancyId = 0L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, 0L, null, null);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(requestDto));
    }

    @Test
    public void shouldValidateUpdaterRole_throw_whenUpdaterTeamMemberIdIsNotPresented() {
        // Arrange
        var vacancyId = 1L;
        var teamMemberUpdaterId = 0L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, teamMemberUpdaterId, VacancyStatus.POSTPONED, 10);

        var projectId = 1L;
        var vacancy = Vacancy.builder().id(vacancyId).project(getTestProject(projectId)).build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(requestDto));
    }

    @Test
    public void shouldUpdateVacancy_throw_whenUpdaterRoleValidationIsFailed() {
        // Arrange
        var vacancyId = 1L;
        var teamMemberUpdaterId = 3L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, teamMemberUpdaterId, VacancyStatus.POSTPONED, 10);

        var projectId = 1L;
        var vacancy = Vacancy.builder().id(vacancyId).project(getTestProject(projectId)).build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        var updater = getTestUpdater(teamMemberUpdaterId);
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId)).thenReturn(Optional.of(updater));

        Mockito.doThrow(new DataValidationException("Invalid updater role")).when(updateVacancyRequestValidator)
                .validateUpdaterRole(updater);

        // Act + Assert
        assertThrowsExactly(DataValidationException.class, () -> vacancyService.updateVacancy(requestDto),
                "Invalid updater role");
    }


    @Test
    public void shouldUpdateVacancy_throw_whenCandidatesCountValidationIsFailed() {
        // Arrange
        var vacancyId = 1L;
        var teamMemberUpdaterId = 3L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, teamMemberUpdaterId, VacancyStatus.POSTPONED, 10);

        var projectId = 1L;
        var vacancy = Vacancy.builder().id(vacancyId).project(getTestProject(projectId)).build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        var updater = getTestUpdater(teamMemberUpdaterId);
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId)).thenReturn(Optional.of(updater));

        List<Candidate> attachedToProjectCandidates = List.of();
        when(candidateService.getAllCandidatesAttachedToProjectVacancy(vacancyId, projectId))
                .thenReturn(attachedToProjectCandidates);

        Mockito.doThrow(new DataValidationException("Invalid candidates count")).when(updateVacancyRequestValidator)
                .validateCandidatesCount(requestDto, vacancy, attachedToProjectCandidates);

        // Act + Assert
        assertThrowsExactly(DataValidationException.class, () -> vacancyService.updateVacancy(requestDto),
                "Invalid candidates count");
    }

    @Test
    public void shouldUpdateVacancy_returnVacancy_whenDataIsValid() {
        // Arrange
        var vacancyId = 1L;
        var teamMemberUpdaterId = 2L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, teamMemberUpdaterId, VacancyStatus.CLOSED, 10);

        var projectId = 1L;
        var project = getTestProject(projectId);
        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(Optional.of(project));

        var updater = getTestUpdater(teamMemberUpdaterId);
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId)).thenReturn(Optional.of(updater));

        var vacancy = Vacancy.builder()
                .id(vacancyId)
                .project(project)
                .createdBy(2L)
                .updatedBy(teamMemberUpdaterId)
                .build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

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
    public void shouldUpdateVacancy_returnVacancy_whenNoChangesAreNeeded() {
        // Arrange
        var vacancyId = 1L;
        var teamMemberUpdaterId = 2L;
        var requestDto = UpdateVacancyRequestDto.builder()
                .vacancyId(vacancyId)
                .teamMemberUpdaterId(teamMemberUpdaterId)
                .build();

        var projectId = 1L;
        var project = getTestProject(projectId);
        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(Optional.of(project));

        var updater = getTestUpdater(teamMemberUpdaterId);
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId)).thenReturn(Optional.of(updater));

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
                .createdBy(2L)
                .updatedBy(teamMemberUpdaterId)
                .build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

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
    public void shouldGetFilteredVacancies_returnsOriginalRequests_whenAllFiltersAreNotApplicable() {
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

        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(Optional.of(author));

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
    public void shouldGetFilteredVacancies_returnsEmptyList_whenAllRequestsAreNotMatched() {
        // Arrange
        var filterDto = new FilterVacancyRequestDto(null, "Test");

        var vacancies = List.of(
                Vacancy.builder().id(1L).name("Java Developer").build(),
                Vacancy.builder().id(2L).name("Kotlin Developer").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);

        setupVacancyFilter(vacancyFilter1, filterDto, true, new ReturnEmptyStreamVacancyAnswer());
        setupVacancyFilter(vacancyFilter2, filterDto, true, new ReturnEmptyStreamVacancyAnswer());

        // Act
        var result = vacancyService.getFilteredVacancies(filterDto);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldGetFilteredVacancies_returnsNotEmptyList_whenSomeRequestsAreMatched() {
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

        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(Optional.of(author));

        setupVacancyFilter(vacancyFilter1, filterDto, true,
                new AdjustableVacancyAnswer(vacancy -> vacancy.getName().contains(namePatternToSearch)));
        setupVacancyFilter(vacancyFilter2, filterDto, true,
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
    public void shouldGetVacancyById_throw_whenRequestIsNotFound() {
        var vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        var result = vacancyService.getVacancyById(vacancyId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldGetVacancyById_returnsVacancy_whenRequestIsFound() {
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
        when(projectService.getProjectByIdOrEmpty(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(Optional.of(author));

        // Act
        var result = vacancyService.getVacancyById(vacancyId);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(vacancy.getId(), result.get().getId());
        assertEquals(vacancy.getName(), result.get().getName());
        assertEquals(vacancy.getProject().getName(), result.get().getProjectName());
        assertEquals(author.getNickname(), result.get().getCreatedByNickname());
        assertNotNull(result.get().getCandidates());
        assertEquals(candidates.size(), result.get().getCandidates().size());
        assertEquals(candidates.get(0).getUsername(), result.get().getCandidates().get(0).username());
        assertEquals(candidates.get(0).getCandidateStatus(), result.get().getCandidates().get(0).candidateStatus());
        verify(projectService, times(1)).getProjectByIdOrEmpty(project.getId());
        verify(teamMemberService, times(1)).getTeamMemberById(authorId);
    }

    @Test
    public void testAddOrChangeCoverToVacancy_throw_vacancyNotFoundException() {
        long vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class,
                () -> vacancyService.addOrChangeCoverToVacancy(vacancyId,
                        createMockFile(200, 200)));

    }

    @Test
    public void testAddOrChangeCoverToVacancy_throw_userNotAllowedException() {
        long vacancyId = 1L;
        long userId = 1L;
        long createdById = 2L;
        long ownerId = 3L;
        Project project = Project.builder().id(1L).ownerId(ownerId).build();
        Vacancy vacancy = Vacancy.builder().id(vacancyId).createdBy(createdById)
                .project(project).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        assertThrows(ResourceForbiddenException.class,
                () -> vacancyService.addOrChangeCoverToVacancy(vacancyId,
                        createMockFile(200, 200)));
    }

    @Test
    public void testAddOrChangeCoverToVacancy_throw_fileTooLongException() {
        long vacancyId = 1L;
        long createdById = 2L;
        long ownerId = 3L;
        Project project = Project.builder().id(1L).ownerId(ownerId).build();
        Vacancy vacancy = Vacancy.builder().id(vacancyId).createdBy(createdById)
                .project(project).build();
        when(userContext.getUserId()).thenReturn(createdById);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        DataValidationException exception = assertThrowsExactly(DataValidationException.class,
                () -> vacancyService.addOrChangeCoverToVacancy(vacancyId,
                        createMockFile(600, 400)));
        assertEquals("Image is too big or too long", exception.getMessage());
    }

    @Test
    public void testAddOrChangeCoverToVacancy_WhenCoverIsExist() {
        long vacancyId = 1L;
        long createdById = 2L;
        long ownerId = 3L;
        Project project = Project.builder().id(1L).ownerId(ownerId).build();
        Vacancy vacancy = Vacancy.builder().id(vacancyId).createdBy(createdById).name("vacancy")
                .project(project).coverImageKey("old cover").build();
        MockMultipartFile cover = createMockFile(10, 10);
        String expectedFolder = "cover_for_vacancy_" + vacancy.getId() + vacancy.getName();
        assert cover != null;
        String newCoverImageKey = expectedFolder + "/" + cover.getOriginalFilename();

        when(userContext.getUserId()).thenReturn(ownerId);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.uploadFile(Objects.requireNonNull(cover),expectedFolder))
                .thenReturn(newCoverImageKey);

        String resultCoverImageKey = vacancyService.addOrChangeCoverToVacancy(vacancyId,cover);
        verify(s3Service, times(1))
                .deleteFile(coverImageKeyCaptor.capture());
        String oldCoverImageKey = coverImageKeyCaptor.getValue();
        assertEquals("old cover", oldCoverImageKey);
        verify(vacancyRepository,times(1)).findById(vacancyId);
        verify(s3Service,times(1)).uploadFile(cover,expectedFolder);
        verify(userContext,times(1)).getUserId();
        verify(vacancyRepository,times(1)).save(vacancy);
        assertEquals(newCoverImageKey,resultCoverImageKey);
    }

    @Test
    public void testAddOrChangeCoverToVacancy_WhenCoverNotExist() {
        long vacancyId = 1L;
        long createdById = 2L;
        long ownerId = 3L;
        Project project = Project.builder().id(1L).ownerId(ownerId).build();
        Vacancy vacancy = Vacancy.builder().id(vacancyId).createdBy(createdById).name("vacancy")
                .project(project).build();
        MockMultipartFile cover = createMockFile(10, 10);
        String expectedFolder = "cover_for_vacancy_" + vacancy.getId() + vacancy.getName();
        assert cover != null;
        String newCoverImageKey = expectedFolder + "/" + cover.getOriginalFilename();

        when(userContext.getUserId()).thenReturn(ownerId);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.uploadFile(Objects.requireNonNull(cover),expectedFolder))
                .thenReturn(newCoverImageKey);

        String resultCoverImageKey = vacancyService.addOrChangeCoverToVacancy(vacancyId,cover);
        verify(s3Service, never())
                .deleteFile(anyString());
        verify(vacancyRepository,times(1)).findById(vacancyId);
        verify(s3Service,times(1)).uploadFile(cover,expectedFolder);
        verify(userContext,times(1)).getUserId();
        verify(vacancyRepository,times(1)).save(vacancy);
        assertEquals(newCoverImageKey,resultCoverImageKey);
    }

    @Test
    public void testGetVacancyCover_throw_WhenVacancyNotFound() {
        long vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        assertThrowsExactly(RecordNotFoundException.class,
                () -> vacancyService.getVacancyCover(vacancyId));

    }

    @Test
    public void testGetVacancyCover_throw_WhenFileNotFound() {
        long vacancyId = 1L;
        String coverImageKey = "cover";
        Vacancy vacancy = Vacancy.builder().id(vacancyId).coverImageKey(coverImageKey).build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.downloadFile(vacancy.getCoverImageKey())).thenThrow(AmazonS3Exception.class);

        assertThrowsExactly(AmazonS3Exception.class,
                ()->vacancyService.getVacancyCover(vacancyId));
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyRepository,times(1)).findById(vacancyId);
    }

    @Test
    public void testGetVacancyCover() {
        long vacancyId = 1L;
        String coverImageKey = "cover";
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream("file-content".getBytes()));
        Vacancy vacancy = Vacancy.builder().id(vacancyId).coverImageKey(coverImageKey).build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.downloadFile(vacancy.getCoverImageKey())).thenReturn(s3Object.getObjectContent());

        InputStream inputStream = vacancyService.getVacancyCover(vacancyId);

        assertNotNull(inputStream);
        verify(vacancyRepository,times(1)).findById(vacancyId);
        verify(s3Service, times(1)).downloadFile(vacancy.getCoverImageKey());
        verify(vacancyRepository, never()).save(vacancy);
    }

    @Test
    public void testDeleteCoverFromVacancy_throw_WhenVacancyNotFound() {
        long vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        assertThrowsExactly(RecordNotFoundException.class,
                () -> vacancyService.deleteCoverFromVacancy(vacancyId));

    }

    @Test
    public void testDeleteCoverFromVacancy_throw_userNotAllowedException() {
        long vacancyId = 1L;
        long userId = 1L;
        long createdById = 2L;
        long ownerId = 3L;
        Project project = Project.builder().id(1L).ownerId(ownerId).build();
        Vacancy vacancy = Vacancy.builder().id(vacancyId).createdBy(createdById)
                .project(project).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        assertThrowsExactly(ResourceForbiddenException.class,
                () -> vacancyService.deleteCoverFromVacancy(vacancyId));
    }

    @Test
    public void testDeleteCoverFromVacancy() {
        long vacancyId = 1L;
        long createdById = 2L;
        long ownerId = 3L;
        String coverImageKey = "cover";
        Project project = Project.builder().id(1L).ownerId(ownerId).build();
        Vacancy vacancy = Vacancy.builder().id(vacancyId).createdBy(createdById)
                .project(project).coverImageKey(coverImageKey).build();
        when(userContext.getUserId()).thenReturn(createdById);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        vacancyService.deleteCoverFromVacancy(vacancyId);
        verify(s3Service,times(1)).deleteFile(coverImageKeyCaptor.capture());
        verify(vacancyRepository,times(1)).save(vacancyCaptor.capture());

        assertEquals(coverImageKey, coverImageKeyCaptor.getValue());
        assertNull(vacancyCaptor.getValue().getCoverImageKey());
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(long projectId, long authorId, Double salary) {
        return OpenVacancyRequestDto.builder()
                .name("Test name")
                .description("Test description")
                .projectId(projectId)
                .position(TeamRole.ANALYST)
                .requiredCandidatesCount(1)
                .authorId(authorId)
                .salary(salary)
                .build();
    }

    private static UpdateVacancyRequestDto createUpdateVacancyRequestDto(long vacancyId, long updaterId,
                                                                         @Nullable VacancyStatus status, @Nullable Integer requiredCandidatesCount) {
        return UpdateVacancyRequestDto.builder()
                .vacancyId(vacancyId)
                .teamMemberUpdaterId(updaterId)
                .name("Test name")
                .description("Test description")
                .position(TeamRole.ANALYST)
                .status(status)
                .requiredCandidatesCount(requiredCandidatesCount)
                .build();
    }

    private static Project getTestProject(long projectId) {
        return Project.builder().id(projectId).build();
    }

    private static TeamMember getTestAuthor(long authorId) {
        return TeamMember.builder().id(authorId).nickname("Author nickname").build();
    }

    private static TeamMember getTestUpdater(long updaterId) {
        return TeamMember.builder().id(updaterId).nickname("Updater nickname").build();
    }

    private void setupVacancyFilter(VacancyFilter filter, FilterVacancyRequestDto filterDto, boolean isApplicable,
                                    Answer<Stream<Vacancy>> filterApplyAnswer) {
        when(filter.isApplicable(filterDto)).thenReturn(isApplicable);
        when(filter.apply(any(), any())).thenAnswer(filterApplyAnswer);
    }

    static MockMultipartFile createMockFile(int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, width, height);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageData = baos.toByteArray();
            return new MockMultipartFile(
                    "cover",
                    "cover.png",
                    "multipart/form-data",
                    imageData
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}