package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageInvitationFilterDto {
    private Long teamMember;
    private StageInvitationStatus status;
    //+ author, stage
}
