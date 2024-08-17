package faang.school.projectservice.servise;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
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
public class StageInvitationServiceTest {

    @Mock
    StageInvitationRepository stageInvitationRepository;

    @InjectMocks
    StageInvitationService stageInvitationService;

    Project project = Project.builder()
            .name("OurProject")
            .build();

    TeamMember author = TeamMember.builder()
            .id(1L)
            .build();

    TeamMember invited = TeamMember.builder()
            .id(10L)
            .build();

    Stage stage = Stage.builder()
            .stageId(1L)
            .stageName("OurStage")
            .project(project)
            .executors(List.of(author))
            .build();

    StageRoles stageRoles = StageRoles.builder()
            .teamRole(TeamRole.DEVELOPER)
            .count(2)
            .build();

    @Test
    void testCreateStageInvitation() {
        String invitationMessage = String.format("Invite you to participate in the development stage %s " +
                        "of the project %s for the role %s",
                stage.getStageName(), stage.getProject().getName(), stageRoles.getTeamRole());

        StageInvitation stageInvitation = stageInvitationService.createStageInvitation(invited, stage, stageRoles );

        verify(stageInvitationRepository, times(1)).save(any());
        assertEquals(stageInvitation.getDescription(), invitationMessage);
    }
}
