package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationDto {
    private Long id;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
    private StageInvitationStatus status;
    private String explanation;
}
