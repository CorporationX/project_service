package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StageInvitationDto(
        Long id,
        String description,
        StageInvitationStatus status,
        @NotNull @Positive(message = "stageId must not be null") Long stageId,
        @NotNull @Positive(message = "authorId must not be null") Long authorId,
        @NotNull @Positive(message = "invitedId must not be null") Long invitedId
) {
}
