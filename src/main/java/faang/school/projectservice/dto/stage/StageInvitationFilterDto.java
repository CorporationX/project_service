package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;

@Data
public class StageInvitationFilterDto {
    private StageInvitationStatus status;
    private Long authorId;
}
