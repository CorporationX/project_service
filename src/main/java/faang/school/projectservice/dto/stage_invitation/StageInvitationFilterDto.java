package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;

@Data
public class StageInvitationFilterDto {
    private Long id;
    private Long stageId;
    private Long authorId;
    private StageInvitationStatus status;
    private int page;
    private int pageSize;
}
