package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Stage Invitation filter entity")
public class StageInvitationFilterDto {
    private Long teamMemberPattern;
    private StageInvitationStatus status;
}
