package faang.school.projectservice.servise;

import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.StageRolesService;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageRolesServiceTest {
    @Mock
    StageRolesRepository stageRolesRepository;
    @Mock
    StageInvitationService stageInvitationService;
    @Mock
    TeamMemberService teamMembersService;
    @InjectMocks
    StageRolesService stageRolesService;

    Stage stage = Stage.builder()
            .stageId(1L)
            .build();

    TeamMember firstExecutor = TeamMember.builder()
            .id(1L)
            .build();

    TeamMember secondExecutor = TeamMember.builder()
            .id(2L)
            .build();

    TeamMember thirdExecutor = TeamMember.builder()
            .id(3L)
            .build();

    TeamMember fourthExecutor = TeamMember.builder()
            .id(4L)
            .build();

    List<TeamMember> executorsWithTheRole = List.of(firstExecutor, secondExecutor);
    List<TeamMember> projectMembersWithTheSameRole = List.of(thirdExecutor, fourthExecutor);

    StageInvitation stageInvitationToSend1 = StageInvitation.builder()
            .invited(thirdExecutor)
            .build();
    StageInvitation stageInvitationToSend2 = StageInvitation.builder()
            .invited(fourthExecutor)
            .build();

    @Test
    void testSaveAll() {

        stageRolesService.saveAll(Mockito.any());

        verify(stageRolesRepository, times(1)).saveAll(Mockito.any());
    }

    @Test
    void testGetExecutorsForRoleIfTeamMembersForRoleNotEnough() {
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DEVELOPER)
                .count(5)
                .build();
        when(teamMembersService.getTeamMembersWithTheRole(stage, stageRoles))
                .thenReturn(executorsWithTheRole);
        when(teamMembersService.getProjectMembersWithTheSameRole(stage, stageRoles))
                .thenReturn(projectMembersWithTheSameRole);
        when(stageInvitationService.createStageInvitation(thirdExecutor, stage, stageRoles))
                .thenReturn(stageInvitationToSend1);
        when(stageInvitationService.createStageInvitation(fourthExecutor, stage, stageRoles))
                .thenReturn(stageInvitationToSend2);

        stageRolesService.getExecutorsForRole(stage, stageRoles);

        verify(stageInvitationService, times(1)).createStageInvitation(thirdExecutor, stage, stageRoles);
        verify(stageInvitationService, times(1)).createStageInvitation(fourthExecutor, stage, stageRoles);
    }

    @Test
    void testGetExecutorsForRoleIfTeamMembersForRoleEnough() {
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DEVELOPER)
                .count(3)
                .build();
        when(teamMembersService.getTeamMembersWithTheRole(stage, stageRoles))
                .thenReturn(executorsWithTheRole);
        when(teamMembersService.getProjectMembersWithTheSameRole(stage, stageRoles))
                .thenReturn(projectMembersWithTheSameRole);
        when(stageInvitationService.createStageInvitation(thirdExecutor, stage, stageRoles))
                .thenReturn(stageInvitationToSend1);

        stageRolesService.getExecutorsForRole(stage, stageRoles);

        verify(stageInvitationService, times(1)).createStageInvitation(thirdExecutor, stage, stageRoles);
    }
}
