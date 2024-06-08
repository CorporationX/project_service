package faang.school.projectservice.dto.stage_invintation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Stage Invitation entity")
public class StageInvitationDto {
    private Long id;
    private String description;
    private Long authorId;
    private Long invitedId;
    private StageInvitationStatus status;
}
