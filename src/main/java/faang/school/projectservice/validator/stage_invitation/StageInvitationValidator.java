package faang.school.projectservice.validator.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StageInvitationValidator {
    public void statusPendingCheck(StageInvitation stageInvitation) {
        if (stageInvitation.getStatus().equals(StageInvitationStatus.ACCEPTED)) {
            throw new RuntimeException("Stage invitation with id " + stageInvitation.getId() + " is already ACCEPTED");
        }
        if (stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED)) {
            throw new RuntimeException("Stage invitation with id " + stageInvitation.getId() + " is already REJECTED");
        }
    }
}
