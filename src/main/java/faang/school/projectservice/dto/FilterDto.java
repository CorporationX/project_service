package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;

@Data
public class FilterDto {
    private Long userId;
    private StageInvitationStatus status;
    private String rejectionReason;
    private Long stageId;
    private Long authorId;
}
