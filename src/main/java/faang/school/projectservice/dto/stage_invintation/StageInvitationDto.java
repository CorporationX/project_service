package faang.school.projectservice.dto.stage_invintation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {
    private Long id;
    private String description;
    private Long authorId;
    private Long invitedId;
    private StageInvitationStatus status;
}
