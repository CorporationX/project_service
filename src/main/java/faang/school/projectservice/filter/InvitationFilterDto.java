package faang.school.projectservice.filter;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationFilterDto {
    private Stage stage;
    private TeamMember author;
    private StageInvitationStatus status;
}
