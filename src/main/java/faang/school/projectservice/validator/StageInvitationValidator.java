package faang.school.projectservice.validator;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {

    public void validateInvitedForCreate(long authorId, long invitedId) {

        if (authorId == invitedId) {
            throw new IllegalArgumentException(String.format(
                    "The author of the stage invitation and the person invited to this stage " +
                            "cannot be the same person. " +
                            "\n authorId: %d\n invitedId: %d", authorId, invitedId));
        }
    }

    public void validateStatusPendingCheck(StageInvitation stageInvitation) {
        if (stageInvitation.getStatus().equals(StageInvitationStatus.ACCEPTED)) {
            throw new IllegalArgumentException("The invitation has already been accepted");
        }
        if (stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED)) {
            throw new IllegalArgumentException("The invitation has already been declined");
        }
    }


}
