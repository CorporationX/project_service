package faang.school.projectservice.dto.stage_invitation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationFilterDto {
    private final Long stageId;
    private final Long authorId;
    private final Long invitedId;
}
