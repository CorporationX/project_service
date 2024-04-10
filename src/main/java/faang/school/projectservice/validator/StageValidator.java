package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {
    private final StageRepository stageRepository;

    public void checkStageForExists(long stageId) {
        boolean isExist = stageRepository.exist(stageId);

        if (!isExist) {
            throw new EntityNotFoundException(String.format("Stage not found by id: %s", stageId));
        }
    }

    public void checkStageInvitationPendingStatus(StageInvitation invitation) {
        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Only pending invitations can be accepted");
        }
    }

    public void validate(StageInvitationDto stageInvitationDto) {
        checkStageForExists(stageInvitationDto.getStageId());
    }
}
