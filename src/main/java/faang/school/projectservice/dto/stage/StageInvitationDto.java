package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
    private StageInvitationStatus status;

}
