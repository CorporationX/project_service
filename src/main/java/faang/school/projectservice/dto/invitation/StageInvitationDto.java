package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {
    @Min(1)
    private Long id;

    private String status;

    private Long stageId;

    private Long authorId;

    private Long invitedId;
}