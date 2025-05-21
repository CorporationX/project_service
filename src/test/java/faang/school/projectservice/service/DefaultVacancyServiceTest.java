package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CandidateDto;
import faang.school.projectservice.dto.CreateCandidateDto;
import faang.school.projectservice.dto.CreateVacancyDto;
import faang.school.projectservice.dto.DetailedVacancyDto;
import faang.school.projectservice.dto.UpdateVacancyDto;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.event.DomainEventPublisher;
import faang.school.projectservice.event.VacancyClosedEvent;
import faang.school.projectservice.event.VacancyCreatedEvent;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.BusinessValidationException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.mapper.CandidateMapperImpl;
import faang.school.projectservice.mapper.CandidateTeamMemberMapperImpl;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultVacancyServiceTest {
    private static final Long VACANCY_ID = 1L;
    private static final String VACANCY_NAME = "Java Developer";
    private static final String VACANCY_DESCRIPTION = "example vacancy description";
    private static final Double SALARY = 500_000.00;
    private static final Long PROJECT_ID = 13L;
    private static final TeamRole VACANCY_POSITION = TeamRole.DEVELOPER;
    private static final Long USER_ID = 25L;
    private static final String USER_NAME = "Some user name";

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private UserContext userContext;
    @Spy
    private VacancyMapperImpl vacancyMapper;
    @Spy
    private CandidateMapperImpl candidateMapper;
    @Spy
    private CandidateTeamMemberMapperImpl candidateTeamMemberMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private CandidateService candidateService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private DomainEventPublisher eventPublisher;
    @InjectMocks
    private DefaultVacancyService vacancyService;

    private final CreateVacancyDto createVacancyDto = new CreateVacancyDto();
    private final Project project = new Project();
    private VacancyDto vacancyDto = new VacancyDto();
    private Vacancy vacancy = new Vacancy();

    @BeforeEach
    void setUp() {
        project.setId(PROJECT_ID);

        createVacancyDto.setName(VACANCY_NAME);
        createVacancyDto.setDescription(VACANCY_DESCRIPTION);
        createVacancyDto.setPosition(VACANCY_POSITION);
        createVacancyDto.setProjectId(PROJECT_ID);
        createVacancyDto.setSalary(SALARY);
        createVacancyDto.setWorkSchedule(WorkSchedule.FULL_TIME);

        vacancy = vacancyMapper.toEntity(createVacancyDto);
        vacancy.setId(VACANCY_ID);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);

        vacancyDto = vacancyMapper.toDto(vacancy);

    }

    @Nested
    @DisplayName("tests for create() method")
    class CreateTests {
        @BeforeEach
        void setUp() {
            when(userContext.getUserId()).thenReturn(USER_ID);
        }

        @Test
        @DisplayName("create() success returns correct DTO and publishes VacancyCreatedEvent")
        void testCreateSuccess() {
            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID)).thenReturn(Set.of(TeamRole.OWNER));
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(vacancyRepository.save(any(Vacancy.class))).thenAnswer(invocation -> {
                Vacancy toSave = invocation.getArgument(0);
                toSave.setId(VACANCY_ID);
                return toSave;
            });

            DetailedVacancyDto result = vacancyService.create(createVacancyDto);

            assertNotNull(result, "Resulting vacancyDto should not be null");
            assertEquals(VACANCY_NAME, result.getName(),
                    "Resulting vacancyDto should have correct name");
            assertEquals(VACANCY_DESCRIPTION, result.getDescription(),
                    "Resulting vacancyDto should have correct description");
            assertEquals(VACANCY_POSITION, result.getPosition(),
                    "Resulting vacancyDto should have correct position");
            assertEquals(PROJECT_ID, result.getProjectId());
            assertEquals(VacancyStatus.OPEN, result.getStatus(), "Status should be OPEN");
            verify(vacancyMapper, times(2)).toEntity(createVacancyDto);
            verify(projectService).getProjectById(PROJECT_ID);
            verify(vacancyRepository).save(vacancy);
            verify(vacancyMapper).toDetailedDto(vacancy);
            verify(eventPublisher).publishEvent(argThat(event ->
                    event instanceof VacancyCreatedEvent &&
                            ((VacancyCreatedEvent) event).getVacancyId().equals(vacancy.getId()) &&
                            ((VacancyCreatedEvent) event).getProjectId().equals(vacancy.getProject().getId()) &&
                            ((VacancyCreatedEvent) event).getPosition().equals(vacancy.getPosition()) &&
                            event.getEventType().equals(VacancyStatus.OPEN)));
        }

        @Test
        @DisplayName("create() user with no permission should throw AccessDeniedException")
        void testCreateUnauthorizedException() {
            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID)).thenReturn(Set.of(VACANCY_POSITION));
            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> vacancyService.create(createVacancyDto),
                    "Expected create() to throw AccessDeniedException when user doesn't have" +
                            "permission to create vacancy");
            assertEquals("Need OWNER or MANAGER", ex.getMessage());
            verifyNoMoreInteractions(projectService, vacancyRepository, eventPublisher);
        }
    }

    @Nested
    @DisplayName("tests for update() method")
    class UpdateTests {
        @BeforeEach
        void setUp() {
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID)).thenReturn(Set.of(TeamRole.OWNER));
            when(userContext.getUserId()).thenReturn(USER_ID);
        }

        @Test
        @DisplayName("update() success with non-null dto returns updated VacancyDto and saves Vacancy")
        void testUpdateSuccess() {
            UpdateVacancyDto updateVacancyDto = new UpdateVacancyDto();
            updateVacancyDto.setDescription("new description");
            updateVacancyDto.setSalary(1_000_000.00);
            updateVacancyDto.setWorkSchedule(WorkSchedule.PART_TIME);
            updateVacancyDto.setCount(10);

            DetailedVacancyDto result = vacancyService.update(VACANCY_ID, updateVacancyDto);

            assertNotNull(result,
                    "Resulting vacancyDto should not be null");
            assertEquals(VACANCY_ID, result.getId(),
                    "Resulting vacancyDto should have correct id");
            assertEquals("new description", result.getDescription(),
                    "Resulting vacancyDto should have updated description");
            assertEquals(1_000_000.00, result.getSalary(),
                    "Resulting vacancyDto should have updated salary");
            assertEquals(WorkSchedule.PART_TIME, result.getWorkSchedule(),
                    "Resulting vacancyDto should have updated work schedule");
            assertEquals(10, result.getCount(),
                    "Resulting vacancyDto should have updated count");
            verify(vacancyRepository).save(vacancy);
        }

        @Test
        @DisplayName("update() success updated only for non-null dto fields")
        void testUpdateSuccessWithNullFields() {
            Double newSalary = 1_000_000.00;

            UpdateVacancyDto updateVacancyDto = new UpdateVacancyDto();
            updateVacancyDto.setSalary(newSalary);

            DetailedVacancyDto result = vacancyService.update(VACANCY_ID, updateVacancyDto);

            assertNotNull(result,
                    "Resulting vacancyDto should not be null");
            assertEquals(VACANCY_ID, result.getId(),
                    "Resulting vacancyDto should have correct id");
            assertEquals(newSalary, result.getSalary(),
                    "Resulting vacancyDto should have updated salary");
            verify(vacancyRepository).save(vacancy);
        }

        @Test
        @DisplayName("update() null dto returns existing VacancyDto without saving")
        void testUpdateNullDto() {
            DetailedVacancyDto result = vacancyService.update(VACANCY_ID, null);

            assertNotNull(result, "Resulting vacancyDto should not be null");
            assertEquals(VACANCY_ID, result.getId(), "Resulting vacancyDto should have correct id");
            verify(vacancyRepository, never()).save(vacancy);
        }
    }

    @Nested
    @DisplayName("tests for addCandidate() method")
    class AddCandidateTests {
        private final CreateCandidateDto createCandidateDto = new CreateCandidateDto();

        @BeforeEach
        void setUp() {
            createCandidateDto.setUserId(USER_ID);
            createCandidateDto.setUsername(USER_NAME);

            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
            when(userContext.getUserId()).thenReturn(USER_ID);
        }

        @Test
        @DisplayName("should persist new candidate when when current user == candidate and " +
                "user is not already a project member")
        void testAddCandidateSuccess() {
            when(teamMemberService.isMember(PROJECT_ID, USER_ID)).thenReturn(false);
            when(candidateRepository.save(any(Candidate.class))).thenReturn(new Candidate());

            CandidateDto result = vacancyService.addCandidate(VACANCY_ID, createCandidateDto);

            assertNotNull(result, "Resulting candidateDto should not be null");
            assertEquals(USER_ID, result.getUserId(), "Resulting candidateDto should have correct userId");
            assertEquals(CandidateStatus.WAITING_RESPONSE, result.getCandidateStatus());
            assertEquals(VACANCY_ID, result.getVacancyId());
            verify(vacancyRepository).save(vacancy);
        }

        @Test
        @DisplayName("should throw BusinessValidationException when user is already a project member")
        void testAddCandidateWhenUserAlreadyMember() {
            when(teamMemberService.isMember(PROJECT_ID, USER_ID)).thenReturn(true);

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> vacancyService.addCandidate(VACANCY_ID, createCandidateDto));

            assertEquals("User %d already member of project %d".formatted(USER_ID, PROJECT_ID),
                    ex.getMessage());
            verify(vacancyRepository, never()).save(vacancy);
        }

        @Test
        @DisplayName("should allow owner / manager to update candidate")
        void testAddCandidateByOwnerOrManager() {
            Long newUserId = USER_ID + 1;
            createCandidateDto.setUserId(newUserId);
            when(teamMemberService.isMember(PROJECT_ID, newUserId)).thenReturn(false);
            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID)).thenReturn(Set.of(TeamRole.OWNER, TeamRole.MANAGER));
            when(candidateRepository.save(any(Candidate.class))).thenReturn(new Candidate());

            CandidateDto result = vacancyService.addCandidate(VACANCY_ID, createCandidateDto);

            assertNotNull(result, "Resulting candidateDto should not be null");
            assertEquals(newUserId, result.getUserId(), "Resulting candidateDto should have correct userId");
            assertEquals(CandidateStatus.WAITING_RESPONSE, result.getCandidateStatus());
            assertEquals(VACANCY_ID, result.getVacancyId());
            verify(vacancyRepository).save(vacancy);
        }
    }

    @Nested
    @DisplayName("tests for close() method")
    class CloseVacancyTests {
        private Candidate cand1;
        private Candidate cand2;
        private Candidate cand3;
        @BeforeEach
        void setUp() {
            cand1 = new Candidate(); cand1.setId(111L);
            cand1.setCandidateStatus(CandidateStatus.ACCEPTED);
            cand1.setVacancy(vacancy);
            cand2 = new Candidate(); cand2.setId(222L);
            cand2.setCandidateStatus(CandidateStatus.ACCEPTED);
            cand2.setVacancy(vacancy);
            cand3 = new Candidate(); cand3.setId(333L);
            cand3.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
            cand3.setVacancy(vacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        }

        @Test
        @DisplayName("should close vacancy, publish event and remove rejected")
        void testCloseSuccess() {
            vacancy.setCount(2);
            vacancy.setCandidates(List.of(cand1, cand2, cand3));

            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID)).thenReturn(Set.of(TeamRole.OWNER, TeamRole.MANAGER));

            DetailedVacancyDto result = vacancyService.close(VACANCY_ID);

            assertNotNull(result, "Resulting vacancyDto should not be null");
            assertEquals(VacancyStatus.CLOSED, result.getStatus(), "Status should be CLOSED");
            assertEquals(VACANCY_ID, result.getId(), "Resulting vacancyDto should have correct id");

            verify(vacancyRepository).save(vacancy);
            verify(eventPublisher).publishEvent(any(VacancyClosedEvent.class));

            ArgumentCaptor<VacancyClosedEvent> eventCap = ArgumentCaptor.forClass(VacancyClosedEvent.class);
            verify(eventPublisher).publishEvent(eventCap.capture());
            VacancyClosedEvent ev = eventCap.getValue();
            assertEquals(VACANCY_ID, ev.getVacancyId());
            assertEquals(PROJECT_ID, ev.getProjectId());

            verify(candidateService).removeRejectedCandidates(
                    eq(VACANCY_ID), eq(List.of(333L)));
        }

        @Test
        @DisplayName("should throw BusinessValidationException when not enough candidates")
        void testCloseNotEnoughCandidates() {
            vacancy.setCount(3);
            vacancy.setCandidates(List.of(cand1, cand2));

            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID)).thenReturn(Set.of(TeamRole.OWNER, TeamRole.MANAGER));

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> vacancyService.close(VACANCY_ID),
                    "Expected close() to throw BusinessValidationException when not enough candidates");

            assertEquals("Not enough candidates for vacancy %d".formatted(VACANCY_ID), ex.getMessage());
            verify(vacancyRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
            verify(candidateService, never()).removeRejectedCandidates(any(), any());
        }

        @Test
        @DisplayName("should throw BusinessValidationException when not enough accepted candidates")
        void testCloseNotEnoughAcceptedCandidates() {
            vacancy.setCount(2);
            vacancy.setCandidates(List.of(cand1, cand3));

            when(teamMemberService.getUserRoles(PROJECT_ID, USER_ID))
                    .thenReturn(Set.of(TeamRole.OWNER, TeamRole.MANAGER));

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> vacancyService.close(VACANCY_ID),
                    "Expected close() to throw BusinessValidationException when not enough accepted candidates");

            assertEquals("Not enough accepted candidates for vacancy %d"
                    .formatted(VACANCY_ID), ex.getMessage());
            verify(vacancyRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
            verify(candidateService, never()).removeRejectedCandidates(any(), any());
        }
    }

    @Nested
    @DisplayName("tests for getById() method")
    class GetVacancyById {
        @Test
        @DisplayName("getById() should return DetailedVacancyDto when vacancy exists")
        void testGetByIdSuccess() {
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));

            DetailedVacancyDto result = vacancyService.getById(VACANCY_ID);

            assertNotNull(result, "Result should not be null");
            assertEquals(VACANCY_ID, result.getId(), "Returned DTO should have the correct ID");

            verify(vacancyRepository).findById(VACANCY_ID);
            verify(vacancyMapper).toDetailedDto(vacancy);
        }

        @Test
        @DisplayName("getById() should throw VacancyNotFoundException when vacancy does not exist")
        void testGetByIdNotFound() {
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

            VacancyNotFoundException exception = assertThrows(
                    VacancyNotFoundException.class,
                    () -> vacancyService.getById(VACANCY_ID),
                    "Expected VacancyNotFoundException to be thrown");

            assertEquals("Vacancy with id=%d not found".formatted(VACANCY_ID), exception.getMessage());

            verify(vacancyRepository).findById(VACANCY_ID);
            verify(vacancyMapper, never()).toDetailedDto(any());
        }
    }
    @Nested
    @DisplayName("tests for getAll() method")
    class GetAllTests {
        private Pageable pageable;
        private List<Vacancy> vacancies;
        private Page<Vacancy> vacancyPage;
        private List<VacancyDto> vacancyDtos;

        @BeforeEach
        void setUp() {
            pageable = PageRequest.of(0, 10);

            vacancies = List.of(vacancy);
            vacancyPage = new PageImpl<>(vacancies, pageable, vacancies.size());

            vacancyDtos = List.of(vacancyDto);
        }

        @Test
        @DisplayName("getAll() should return all vacancies when filter is null")
        void testGetAllFilterNull() {
            when(vacancyRepository.findAll(pageable)).thenReturn(vacancyPage);

            Page<VacancyDto> result = vacancyService.getAll(null, pageable);

            assertNotNull(result, "Result page should not be null");
            assertEquals(vacancyDtos.size(), result.getContent().size(),
                    "Result page should contain the correct number of DTOs");

            verify(vacancyRepository).findAll(pageable);
            verify(vacancyRepository, never()).findAllByFilter(any(), any(), any());
            verify(vacancyMapper, times(vacancies.size() + 1)).toDto(any(Vacancy.class));
        }

        @Test
        @DisplayName("getAll() should return filtered vacancies when filter is provided")
        void testGetAllFilterNotNull() {
            VacancyFilterDto filter = new VacancyFilterDto();
            filter.setName("Java Developer");
            filter.setPosition(VACANCY_POSITION);

            when(vacancyRepository.findAllByFilter(filter.getName(), filter.getPosition(), pageable))
                    .thenReturn(vacancyPage);

            Page<VacancyDto> result = vacancyService.getAll(filter, pageable);

            assertNotNull(result, "Result page should not be null");
            assertEquals(vacancyDtos.size(), result.getContent().size(),
                    "Result page should contain the correct number of DTOs");
            assertEquals(VACANCY_NAME, result.getContent().get(0).getName());

            verify(vacancyRepository, never()).findAll(any(Pageable.class));
            verify(vacancyRepository).findAllByFilter(filter.getName(), filter.getPosition(), pageable);
            verify(vacancyMapper, times(vacancies.size() + 1))
                    .toDto(any(Vacancy.class));
        }
    }
}
