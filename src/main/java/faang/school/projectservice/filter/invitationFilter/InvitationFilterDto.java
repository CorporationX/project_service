package faang.school.projectservice.filter.invitationFilter;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationFilterDto {
    private Long stageId;
    private Long authorId;
    private StageInvitationStatus status;
}
