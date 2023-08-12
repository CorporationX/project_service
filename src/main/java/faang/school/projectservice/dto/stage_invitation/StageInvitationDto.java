package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationDto {
    private final Long id;
    private final String description;
    private final StageInvitationStatus status;
    private final Long stageId;
    private final Long authorId;
    private final Long invitedId;
}
