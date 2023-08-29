package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationFilterDto {
    private String statusPattern;
    private String stagePattern;
    private Long authorPattern;
    private Long invitedPattern;
}