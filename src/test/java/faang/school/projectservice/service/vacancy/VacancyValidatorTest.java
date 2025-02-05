package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {

    @Mock
    private TeamMemberService teamMemberService;
    @InjectMocks
    private VacancyValidator vacancyValidator;

    @Test
    void validateTutorManagerRole() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.MANAGER
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L)).thenReturn(teamMember);

        Assertions.assertDoesNotThrow(() -> vacancyValidator.validateTutorRole(1L, 1L));
        verify(teamMemberService, times(1)).getTeamMemberByIdAndProjectId(1L, 1L);
    }

    @Test
    void validateTutorOwnerRole() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.OWNER
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L)).thenReturn(teamMember);

        Assertions.assertDoesNotThrow(() -> vacancyValidator.validateTutorRole(1L, 1L));
        verify(teamMemberService, times(1)).getTeamMemberByIdAndProjectId(1L, 1L);
    }

    @Test
    void validateTutorIncorrectRole() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.INTERN
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L)).thenReturn(teamMember);

        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateTutorRole(1L, 1L),
                "1 user does not have permission to add a vacancy");
        verify(teamMemberService, times(1)).getTeamMemberByIdAndProjectId(1L, 1L);
    }

    @Test
    void validateTutorNotFound() {
        when(teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L)).thenReturn(null);

        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateTutorRole(1L, 1L),
                "user 1 not found");
        verify(teamMemberService, times(1)).getTeamMemberByIdAndProjectId(1L, 1L);
    }

    @Test
    void validateTutorRoleIdIsNull() {
        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateTutorRole(null, 1L),
                "id and projectId must not be null");
    }

    @Test
    void validateTutorRoleProjectIdIsNull() {
        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateTutorRole(1L, null),
                "id and projectId must not be null");
    }

    @Test
    void validateTutorRoleIdAndProjectIdIsNull() {
        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateTutorRole(null, null),
                "id and projectId must not be null");
    }

    @Test
    void validateCandidatesCount() {
        List<Candidate> candidates = IntStream.rangeClosed(1, 3)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        Vacancy vacancy = Vacancy.builder()
                .count(3)
                .candidates(candidates)
                .project(Project.builder()
                        .id(1L)
                        .build()
                )
                .position(TeamRole.DEVELOPER)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.INTERN
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        Assertions.assertDoesNotThrow(() -> vacancyValidator.validateCandidatesCount(vacancy));
        verify(teamMemberService, times(3)).getTeamMemberByIdAndProjectId(anyLong(), anyLong());
    }

    @Test
    void validateCandidatesCountIsLess() {
        List<Candidate> candidates = IntStream.rangeClosed(1, 3)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        Vacancy vacancy = Vacancy.builder()
                .count(4)
                .candidates(candidates)
                .project(Project.builder()
                        .id(1L)
                        .build()
                )
                .position(TeamRole.DEVELOPER)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.INTERN
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        Assertions.assertThrows(DataValidationException.class, () -> vacancyValidator.validateCandidatesCount(vacancy),
                "There are not enough candidates. Candidate count: 3");
        verify(teamMemberService, times(3)).getTeamMemberByIdAndProjectId(anyLong(), anyLong());
    }

    @Test
    void validateCandidatesCountIsLessRole() {
        List<Candidate> candidates = IntStream.rangeClosed(1, 3)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        Vacancy vacancy = Vacancy.builder()
                .count(3)
                .candidates(candidates)
                .project(Project.builder()
                        .id(1L)
                        .build()
                )
                .position(TeamRole.DESIGNER)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.INTERN
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        Assertions.assertThrows(DataValidationException.class, () -> vacancyValidator.validateCandidatesCount(vacancy),
                "There are not enough candidates. Candidate count: 3");
        verify(teamMemberService, times(3)).getTeamMemberByIdAndProjectId(anyLong(), anyLong());
    }

    @Test
    void validateCandidatesCountIsMore() {
        List<Candidate> candidates = IntStream.rangeClosed(1, 4)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        Vacancy vacancy = Vacancy.builder()
                .count(3)
                .candidates(candidates)
                .project(Project.builder()
                        .id(1L)
                        .build()
                )
                .position(TeamRole.DEVELOPER)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(
                        TeamRole.ANALYST,
                        TeamRole.DEVELOPER,
                        TeamRole.INTERN
                ))
                .build();

        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        Assertions.assertThrows(DataValidationException.class, () -> vacancyValidator.validateCandidatesCount(vacancy),
                "There are too many candidates. Make a final choice. Candidate count: 3");
        verify(teamMemberService, times(4)).getTeamMemberByIdAndProjectId(anyLong(), anyLong());
    }

    @Test
    void validateCandidatesCountIsVacancyNull() {
        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateCandidatesCount(null), "vacancy must not be null");
    }

    @Test
    void validateVacancyStatus() {
        Vacancy vacancy = Vacancy.builder()
                .status(VacancyStatus.OPEN)
                .build();
        Assertions.assertDoesNotThrow(() -> vacancyValidator.validateVacancyStatus(vacancy));
    }

    @Test
    void validateVacancyStatusIsClosed() {
        Vacancy vacancy = Vacancy.builder()
                .id(3L)
                .status(VacancyStatus.CLOSED)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyValidator.validateVacancyStatus(vacancy),
                "vacancy id 3 has already been closed");
    }

    @Test
    void validateCandidates() {
        List<Candidate> candidates = IntStream.rangeClosed(0, 10).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        List<TeamMember> teamMembers = IntStream.rangeClosed(11, 18).boxed()
                .map(i -> TeamMember.builder()
                        .userId(Long.valueOf(i))
                        .build())
                .toList();

        Vacancy vacancy = Vacancy.builder()
                .project(Project.builder()
                        .teams(List.of(
                                Team.builder()
                                        .teamMembers(teamMembers)
                                        .build()
                        ))
                        .build())
                .build();

        Assertions.assertDoesNotThrow(() -> vacancyValidator.validateCandidates(vacancy, candidates));
    }

    @Test
    void validateCandidatesInProject() {
        List<Candidate> candidates = IntStream.rangeClosed(0, 10).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        List<TeamMember> teamMembers = IntStream.rangeClosed(9, 18).boxed()
                .map(i -> TeamMember.builder()
                        .userId(Long.valueOf(i))
                        .build())
                .toList();

        Vacancy vacancy = Vacancy.builder()
                .project(Project.builder()
                        .teams(List.of(
                                Team.builder()
                                        .teamMembers(teamMembers)
                                        .build()
                        ))
                        .build())
                .build();

        Assertions.assertThrows(DataValidationException.class,
                () -> vacancyValidator.validateCandidates(vacancy, candidates));
    }


}