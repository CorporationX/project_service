package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class StageInvitationDto {
    private Long id;
    private String description;
    private StageInvitationStatus status;

    @NotNull(message = "Stage ID must not be null")
    private Long stageId;
    private Long authorId;

    @NotNull(message = "Invited ID must not be null")
    private Long invitedId;
}
