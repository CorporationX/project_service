package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;

    public StageInvitation createStageInvitation(TeamMember invited, Stage stage, StageRoles stageRoles) {
        StageInvitation stageInvitation = new StageInvitation();
        String INVITATIONS_MESSAGE = String.format("Invite you to participate in the development stage %s " +
                        "of the project %s for the role %s",
                stage.getStageName(), stage.getProject().getName(), stageRoles.getTeamRole());
        stageInvitation.setDescription(INVITATIONS_MESSAGE);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setAuthor(stage.getExecutors().get(0));
        stageInvitation.setInvited(invited);
        stageInvitation.setStage(stage);
        stageInvitationRepository.save(stageInvitation);
        log.info("Stage invitation for the role {} " +
                        "has been sent to team member with ID = {}",
                stageRoles.getTeamRole().name(), invited.getId());
        return stageInvitation;
    }
}
