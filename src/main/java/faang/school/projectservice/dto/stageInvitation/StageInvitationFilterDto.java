package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StageInvitationFilterDto {
    private final Long stageId;
    private final Long authorId;
    private final Long invitedId;
    private final StageInvitationStatus status;
}
