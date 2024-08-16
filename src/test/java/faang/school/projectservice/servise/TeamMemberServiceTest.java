package faang.school.projectservice.servise;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @Mock
    TeamMemberRepository teamMemberRepository;

    @InjectMocks
    TeamMemberService teamMembersService;

    Stage anotherStage = Stage.builder()
            .stageId(2L)
            .build();

    TeamMember firstExecutor = TeamMember.builder()
            .id(1L)
            .roles(List.of(TeamRole.ANALYST))
            .build();

    TeamMember secondExecutor = TeamMember.builder()
            .id(2L)
            .roles(List.of(TeamRole.DEVELOPER))
            .build();

    TeamMember thirdExecutor = TeamMember.builder()
            .id(3L)
            .roles(List.of(TeamRole.TESTER, TeamRole.DEVELOPER))
            .build();

    TeamMember fourthExecutor = TeamMember.builder()
            .id(4L)
            .roles(List.of(TeamRole.DEVELOPER))
            .stages(List.of(anotherStage))
            .build();

    TeamMember fifthExecutor = TeamMember.builder()
            .id(5L)
            .roles(List.of(TeamRole.DESIGNER))
            .stages(List.of(anotherStage))
            .build();

    Team firstTeam = Team.builder()
            .teamMembers(List.of(firstExecutor, secondExecutor, fifthExecutor))
            .build();

    Team secondTeam = Team.builder()
            .teamMembers(List.of(thirdExecutor, fourthExecutor))
            .build();

    Project project = Project.builder()
            .teams(List.of(firstTeam, secondTeam))
            .build();

    StageRoles stageRoles = StageRoles.builder()
            .teamRole(TeamRole.DEVELOPER)
            .build();

    Stage stage = Stage.builder()
            .stageId(1L)
            .project(project)
            .executors(List.of(firstExecutor, secondExecutor, thirdExecutor))
            .build();

    @Test
    void testFindAllById() {

        teamMembersService.findAllById(any());

        verify(teamMemberRepository, times(1)).findAllById(any());
    }

    @Test
    void testGetTeamMembersWithTheRole() {
        List<TeamMember> teamMembersWithTheRole = List.of(secondExecutor, thirdExecutor);

        List<TeamMember> testResult = teamMembersService.getTeamMembersWithTheRole(stage, stageRoles);

        assertEquals(testResult, teamMembersWithTheRole);
    }

    @Test
    void testGetProjectMembersWithTheSameRole() {
        firstExecutor.setStages(List.of(stage));
        secondExecutor.setStages(List.of(stage));
        thirdExecutor.setStages(List.of(stage));
        List<TeamMember> projectMembersWithTheSameRole = List.of(fourthExecutor);

        List<TeamMember> testResult = teamMembersService.getProjectMembersWithTheSameRole(stage, stageRoles);

        assertEquals(testResult, projectMembersWithTheSameRole);
    }
}
