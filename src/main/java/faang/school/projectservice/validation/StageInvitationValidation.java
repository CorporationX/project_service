package faang.school.projectservice.validation;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidation {
    private final StageInvitationRepository stageInvitationRepository;

    public StageInvitation validateInvitationExists(Long invitationId) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(invitationId);
        if ( stageInvitation == null) {
            throw new NotFoundException("Invitation not found with id: " + invitationId);
        }
        return stageInvitation;
    }

    public void validateInvitationPending(StageInvitation stageInvitation) {
        if (stageInvitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Cannot change invitation with status: " + stageInvitation.getStatus());
        }
    }
}
