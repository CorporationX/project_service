package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationDto {

    private Long id;
    private String description;
    private StageInvitationStatus status;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
}
