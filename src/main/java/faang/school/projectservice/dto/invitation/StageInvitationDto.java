package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageInvitationDto {
    private Long id;
    private StageInvitationStatus status;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;
}