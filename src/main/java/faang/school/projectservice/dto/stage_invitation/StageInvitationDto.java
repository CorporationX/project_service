package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationDto {
    private final Long id;
    private final String description;
    private final StageInvitationStatus status;
    @NotNull(message = "stageId can't be null")
    private final Long stageId;
    @NotNull(message = "authorId can't be null")
    private final Long authorId;
    @NotNull(message = "invitedId can't be null")
    private final Long invitedId;
}
