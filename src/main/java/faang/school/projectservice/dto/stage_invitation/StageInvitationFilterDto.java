package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationFilterDto {
    private Long stageId;
    private Long authorId;
    private StageInvitationStatus status;
}
