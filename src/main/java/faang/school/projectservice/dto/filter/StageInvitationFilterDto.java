package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageInvitationFilterDto {
    private Long teamMemberPattern;
    private StageInvitationStatus statusPattern;
}
