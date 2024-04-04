package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationFilterDto {
    private Long teamMemberPattern;
    private StageInvitationStatus statusPattern;
}
