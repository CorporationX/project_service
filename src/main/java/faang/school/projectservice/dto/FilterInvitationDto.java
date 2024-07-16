package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;

@Data
public class FilterInvitationDto {
    private Long userIdPattern;
    private StageInvitationStatus statusPattern;
    private String rejectionReasonPattern;
    private Long stageIdPattern;
    private Long authorIdPattern;
}
