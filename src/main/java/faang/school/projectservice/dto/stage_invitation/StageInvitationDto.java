package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

import faang.school.projectservice.validation.CreateGroup;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record StageInvitationDto(
        Long id,
        @NotNull(message = "Stage ID cannot be null", groups = CreateGroup.class)
        Long stageId,
        @NotNull(message = "Author ID cannot be null", groups = CreateGroup.class)
        Long authorId,
        @NotNull(message = "Invited ID cannot be null", groups = CreateGroup.class)
        Long invitedId,
        StageInvitationStatus status,
        String reason,
        LocalDateTime createdAt
) {
    public StageInvitationDto {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}