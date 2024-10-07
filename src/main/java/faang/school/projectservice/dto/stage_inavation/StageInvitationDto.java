package faang.school.projectservice.dto.stage_inavation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;

public record StageInvitationDto(
        Long id,
        @NotNull(message = "Stage ID не может быть пустым")
        Long stageId,
        @NotNull(message = "Author ID не может быть пустым")
        Long authorId,
        @NotNull(message = "Invited ID не может быть пустым")
        Long invitedId,
        String description,
        StageInvitationStatus status
) {
}