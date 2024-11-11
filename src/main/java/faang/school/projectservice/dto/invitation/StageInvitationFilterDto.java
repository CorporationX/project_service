package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationFilterDto {
    private String descriptionPattern;
    private StageInvitationStatus statusPattern;
    private Long authorIdPattern;
    private Long invitedIdPattern;
}
