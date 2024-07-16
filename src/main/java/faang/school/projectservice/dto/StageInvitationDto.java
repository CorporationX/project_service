package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class StageInvitationDto {
    private Long id;
    private String description;
    private StageInvitationStatus status;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
    private String rejectionReason;
}
