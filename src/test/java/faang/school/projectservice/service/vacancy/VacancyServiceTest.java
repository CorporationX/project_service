package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.TeamRole.TESTER;
import static faang.school.projectservice.model.VacancyStatus.CLOSED;
import static faang.school.projectservice.model.VacancyStatus.OPEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private Project project;
    private Long userId;
    private TeamMember teamMember;
    private Vacancy vacancy;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .id(1L)
                .name("TestProject1")
                .build();
        userId = 111L;
        teamMember = TeamMember.builder()
                .id(1L)
                .userId(userId)
                .build();

        vacancy = Vacancy.builder()
                .id(1L)
                .name("TestVacancy1")
                .description("description")
                .build();
    }

    @Test
    public void create_shouldCreateVacancy_successfully() {
        teamMember.setRoles(Arrays.asList(OWNER, MANAGER));

        Long projectId = project.getId();
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(projectRepository.getReferenceById(projectId)).thenReturn(project);

        ArgumentCaptor<Vacancy> vacancyCaptor = ArgumentCaptor.forClass(Vacancy.class);
        when(vacancyRepository.save(vacancyCaptor.capture()))
                .thenReturn(vacancy);

        vacancyService.create(vacancy, projectId);

        Vacancy capturedVacancy = vacancyCaptor.getValue();
        assertNotNull(capturedVacancy);
        assertEquals(OPEN, capturedVacancy.getStatus());
        assertEquals("TestVacancy1", capturedVacancy.getName());
        assertEquals("description", capturedVacancy.getDescription());
        assertEquals(project, capturedVacancy.getProject());
    }

    @Test
    public void create_shouldCreateVacancy_failure() {
        teamMember.setRoles(Arrays.asList(TESTER, DEVELOPER));

        Long projectId = project.getId();
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        assertThrows(ForbiddenException.class, () -> vacancyService.create(vacancy, projectId));

        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(projectRepository, times(0)).getReferenceById(projectId);
        verify(userContext, times(1)).getUserId();
        verify(vacancyRepository, times(0)).save(any(Vacancy.class));
    }

    @Test
    public void filter_findAllVacancies_returnNull() {
        List<Vacancy> vacancies = new ArrayList<>();

        when((vacancyRepository.findAll())).thenReturn(vacancies);

        assertThrows(EntityNotFoundException.class, () ->
                vacancyService.filterGet(new VacancyFilterDto(DEVELOPER, "Test")));
    }

    @Test
    public void filter_findVacancies_returnSuccessfully() {
        Vacancy vacancy2 = Vacancy.builder()
                .id(2L)
                .name("TestUpdate")
                .position(DEVELOPER)
                .build();
        Vacancy vacancy3 = Vacancy.builder()
                .id(3L)
                .name("TESt1")
                .position(DEVELOPER)
                .build();
        Vacancy vacancy4 = Vacancy.builder()
                .id(4L)
                .name("TestUpdate2")
                .position(MANAGER)
                .build();
        Vacancy vacancy5 = Vacancy.builder()
                .id(5L)
                .name("peek")
                .position(DESIGNER)
                .build();
        Vacancy vacancy6 = Vacancy.builder()
                .id(6L)
                .name("twin")
                .position(TESTER)
                .build();
        List<Vacancy> vacancies = Arrays.asList(vacancy, vacancy5, vacancy2, vacancy3, vacancy4, vacancy6);
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto(DEVELOPER, "test");

        when((vacancyRepository.findAll())).thenReturn(vacancies);

        List<Vacancy> result = vacancyService.filterGet(vacancyFilterDto);

        assertEquals(2, result.size());

    }

    @Test
    public void filter_findVacanciesDtoNull_returnSuccessfully() {
        Vacancy vacancy2 = Vacancy.builder()
                .id(2L)
                .name("TestUpdate")
                .position(DEVELOPER)
                .build();
        Vacancy vacancy3 = Vacancy.builder()
                .id(3L)
                .name("TESt1")
                .position(DEVELOPER)
                .build();
        Vacancy vacancy4 = Vacancy.builder()
                .id(4L)
                .name("TestUpdate2")
                .position(MANAGER)
                .build();
        Vacancy vacancy5 = Vacancy.builder()
                .id(5L)
                .name("peek")
                .position(DESIGNER)
                .build();
        Vacancy vacancy6 = Vacancy.builder()
                .id(6L)
                .name("twin")
                .position(TESTER)
                .build();
        List<Vacancy> vacancies = Arrays.asList(vacancy, vacancy5, vacancy2, vacancy3, vacancy4, vacancy6);
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto(null, null);

        when((vacancyRepository.findAll())).thenReturn(vacancies);

        List<Vacancy> result = vacancyService.filterGet(vacancyFilterDto);

        assertEquals(6, result.size());
    }

    @Test
    public void update_shouldUpdateFilterVacancy_successfully() {

        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();
        project.setTeams(Arrays.asList(team));
        teamMember.setTeam(team);
        teamMember.setRoles(Arrays.asList(OWNER));
        vacancy.setProject(project);
        vacancy.setStatus(OPEN);
        vacancy.setCount(5);

        VacancyUpdateDto vacancyUpdateDto = new VacancyUpdateDto("Update",
                "Update",
                null,
                null,
                null);

        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.getReferenceById(vacancy.getId())).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(teamMember);
        Vacancy result = vacancyService.updateFilter(vacancy.getId(), vacancyUpdateDto);

        assertNotNull(result);
        assertEquals(result.getName(), "Update");
        assertEquals(result.getDescription(), "Update");
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void update_userPositionEqualsDeveloper_exception() {
        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();
        project.setTeams(Arrays.asList(team));
        teamMember.setTeam(team);
        teamMember.setRoles(Arrays.asList(DEVELOPER));
        vacancy.setProject(project);

        VacancyUpdateDto vacancyUpdateDto = new VacancyUpdateDto("Update",
                "Update",
                null,
                null,
                null);
        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.getReferenceById(vacancy.getId())).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(teamMember);

        assertThrows(ForbiddenException.class, () -> vacancyService.updateFilter(vacancy.getId(), vacancyUpdateDto));
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void update_vacancyStatusClosed_exception() {
        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();
        project.setTeams(Arrays.asList(team));
        teamMember.setTeam(team);
        teamMember.setRoles(Arrays.asList(MANAGER));
        vacancy.setProject(project);
        vacancy.setStatus(CLOSED);

        VacancyUpdateDto vacancyUpdateDto = new VacancyUpdateDto("Update",
                "Update",
                null,
                null,
                null);
        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.getReferenceById(vacancy.getId())).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(teamMember);

        assertThrows(ForbiddenException.class, () -> vacancyService.updateFilter(vacancy.getId(), vacancyUpdateDto));
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void update_vacancyCandidatesLessNeedToCount_exception() {
        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();
        project.setTeams(Arrays.asList(team));
        teamMember.setTeam(team);
        teamMember.setRoles(Arrays.asList(MANAGER));
        vacancy.setProject(project);
        vacancy.setCount(5);
        vacancy.setCandidates(Arrays.asList(new Candidate(), new Candidate(), new Candidate()));
        vacancy.setStatus(OPEN);

        VacancyUpdateDto vacancyUpdateDto = new VacancyUpdateDto("Update",
                "Update",
                CLOSED,
                null,
                1L);

        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.getReferenceById(vacancy.getId())).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(teamMember);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> vacancyService.updateFilter(vacancy.getId(), vacancyUpdateDto));

        assertEquals(exception.getMessage(), "We haven't yet recruited enough candidates to fill the vacancy.");
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void update_vacancyCandidatesMoreNeedToCount_success() {
        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();
        project.setTeams(Arrays.asList(team));
        teamMember.setTeam(team);
        teamMember.setRoles(Arrays.asList(MANAGER));
        vacancy.setProject(project);
        vacancy.setCount(2);
        vacancy.setCandidates(Arrays.asList(new Candidate(), new Candidate(), new Candidate()));
        vacancy.setStatus(OPEN);

        VacancyUpdateDto vacancyUpdateDto = new VacancyUpdateDto("Update",
                "Update",
                CLOSED,
                null,
                1L);

        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.getReferenceById(vacancy.getId())).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(teamMember);

        Vacancy result = vacancyService.updateFilter(vacancy.getId(), vacancyUpdateDto);

        assertNotNull(result);
        assertEquals(result.getName(), "Update");
        assertEquals(result.getDescription(), "Update");

        verify(vacancyRepository, times(1)).save(any(Vacancy.class));

    }

}