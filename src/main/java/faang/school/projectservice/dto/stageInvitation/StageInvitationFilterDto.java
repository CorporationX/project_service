package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StageInvitationFilterDto {
    private final Long stageIdPattern;
    private final Long authorIdPattern;
    private final Long invitedIdPattern;
    private final StageInvitationStatus statusPattern;
}
