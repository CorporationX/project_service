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
    @Min(0)
    private Long id;

    private StageInvitationStatus status;

    private Stage stage;

    private TeamMember author;

    private TeamMember invited;
}