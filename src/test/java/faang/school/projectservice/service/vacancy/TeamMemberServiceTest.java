package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.vacancy.EntityNotFoundException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private VacancyRepository vacancyRepository;

    @Spy
    private VacancyValidator vacancyValidator;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private Project project;
    private Team team;
    private TeamMember author;
    private Vacancy vacancy;
    private Candidate candidate;
    private final Long PROJECT_ID = 1L;
    private final Long CANDIDATE_ID = 1L;
    private final Long VACANCY_ID = 1L;
    private final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .id(1L)
                .build();

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        project = Project.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .teams(teams)
                .build();

        author = TeamMember.builder()
                .id(1L)
                .userId(USER_ID)
                .roles(List.of(TeamRole.OWNER))
                .team(team)
                .build();

        candidate = Candidate.builder()
                .id(CANDIDATE_ID)
                .userId(200L)
                .username("candidate_user")
                .build();

        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);

        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .name("Java Developer")
                .position(TeamRole.DEVELOPER)
                .project(project)
                .candidates(candidates)
                .build();
    }

    @Test
    void addCandidateToTeamValidDataShouldAddCandidateToTeam() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(author);
        when(vacancyRepository.getByIdOrThrow(VACANCY_ID)).thenReturn(vacancy);
        when(projectRepository.getByIdOrThrow(PROJECT_ID)).thenReturn(project);

        TeamMember expectedTeamMember = TeamMember.builder()
                .userId(candidate.getUserId())
                .nickname(candidate.getUsername())
                .roles(List.of(TeamRole.DEVELOPER))
                .team(team)
                .build();

        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(expectedTeamMember);

        TeamMember result = teamMemberService.addCandidateToTeam(PROJECT_ID, CANDIDATE_ID, VACANCY_ID);

        assertNotNull(result);
        assertEquals(candidate.getUserId(), result.getUserId());
        assertEquals(candidate.getUsername(), result.getNickname());
        assertEquals(TeamRole.DEVELOPER, result.getRoles().get(0));
        assertEquals(team, result.getTeam());

        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    void addCandidateToTeamCandidateNotFoundInVacancyShouldThrowException() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(author);

        Vacancy vacancyWithoutCandidate = Vacancy.builder()
                .id(VACANCY_ID)
                .candidates(new ArrayList<>())
                .build();
        when(vacancyRepository.getByIdOrThrow(VACANCY_ID)).thenReturn(vacancyWithoutCandidate);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.addCandidateToTeam(PROJECT_ID, CANDIDATE_ID, VACANCY_ID));

        assertEquals(String.format("Candidate with id %s not found in vacancy %s", CANDIDATE_ID, VACANCY_ID),
                exception.getMessage());
    }

    @Test
    void addCandidateToTeamProjectHasNoTeamsShouldThrowException() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(author);
        when(vacancyRepository.getByIdOrThrow(VACANCY_ID)).thenReturn(vacancy);

        Project projectWithoutTeams = Project.builder()
                .id(PROJECT_ID)
                .teams(new ArrayList<>())
                .build();
        when(projectRepository.getByIdOrThrow(PROJECT_ID)).thenReturn(projectWithoutTeams);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> teamMemberService.addCandidateToTeam(PROJECT_ID, CANDIDATE_ID, VACANCY_ID));

        assertEquals("Project has no teams", exception.getMessage());
    }

    @Test
    void addCandidateToTeamUnauthorizedUserShouldThrowException() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(author);

        org.mockito.MockedStatic<VacancyValidator> mockedValidator = org.mockito.Mockito.mockStatic(VacancyValidator.class);
        mockedValidator.when(() -> VacancyValidator.validateRole(author))
                .thenThrow(new SecurityException("User doesn't have required role"));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> teamMemberService.addCandidateToTeam(PROJECT_ID, CANDIDATE_ID, VACANCY_ID));

        assertEquals("User doesn't have required role", exception.getMessage());

        mockedValidator.close();
    }

    @Test
    void addCandidateToTeamCandidateAlreadyProjectMemberShouldThrowException() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(author);
        when(vacancyRepository.getByIdOrThrow(VACANCY_ID)).thenReturn(vacancy);
        when(projectRepository.getByIdOrThrow(PROJECT_ID)).thenReturn(project);

        org.mockito.MockedStatic<VacancyValidator> mockedValidator = org.mockito.Mockito.mockStatic(VacancyValidator.class);
        mockedValidator.when(() -> VacancyValidator.validateCandidateIsAlreadyProjectMember(project, candidate))
                .thenThrow(new IllegalStateException("Candidate is already a project member"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> teamMemberService.addCandidateToTeam(PROJECT_ID, CANDIDATE_ID, VACANCY_ID));

        assertEquals("Candidate is already a project member", exception.getMessage());

        mockedValidator.close();
    }

    @Test
    void addCandidateToTeamValidDataShouldCreateTeamMemberWithCorrectProperties() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(author);
        when(vacancyRepository.getByIdOrThrow(VACANCY_ID)).thenReturn(vacancy);
        when(projectRepository.getByIdOrThrow(PROJECT_ID)).thenReturn(project);

        TeamMember savedTeamMember = TeamMember.builder()
                .id(2L)
                .userId(candidate.getUserId())
                .nickname(candidate.getUsername())
                .roles(List.of(TeamRole.DEVELOPER))
                .team(team)
                .build();

        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(savedTeamMember);

        TeamMember result = teamMemberService.addCandidateToTeam(PROJECT_ID, CANDIDATE_ID, VACANCY_ID);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(candidate.getUserId(), result.getUserId());
        assertEquals(candidate.getUsername(), result.getNickname());
        assertEquals(1, result.getRoles().size());
        assertEquals(TeamRole.DEVELOPER, result.getRoles().get(0));
        assertEquals(team, result.getTeam());
    }
}
