package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class StageInvitationFilterDto {
    private Long authorId;
    private Long invitedId;
    private StageInvitationStatus status;
}
