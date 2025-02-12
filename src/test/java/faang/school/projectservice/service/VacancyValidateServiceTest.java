package faang.school.projectservice.service;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class VacancyValidateServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private VacancyValidatorService validateService;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    void validateCuratorRoleTest() {
        Long curatorId = 1L;
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        Mockito.when(teamMemberRepository.findById(curatorId))
                .thenReturn(Optional.of(teamMember));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                validateService.validateCuratorHasOwnerOrManagerRole(curatorId));
    }

    @Test
    void validateCandidatesNotInProjectTest() {
        List<Long> candidateIds = List.of(1L, 2L, 3L);
        Long projectId = 1L;
        List<Long> projectMembers = List.of(4L, 5L, 3L);
        Mockito.when(projectRepository.findAllTeamMemberIdsByProjectId(projectId))
                .thenReturn(projectMembers);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                validateService.ensureCandidatesAreNotProjectMembers(candidateIds, projectId));
    }

    @Test
    void validateVacancyClosureTest() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCount(2);
        vacancy.getCandidates().add(new Candidate());
        VacancyStatus status = VacancyStatus.CLOSED;
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                validateService.validateVacancyCanBeClosed(vacancy, status));
    }
}
