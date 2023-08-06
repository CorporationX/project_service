package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StageInvitationDto {
    private Long id;
    private String description;
    private StageInvitationStatus status;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
}
