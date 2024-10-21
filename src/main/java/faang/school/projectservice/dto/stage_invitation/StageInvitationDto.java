package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validation.CreateGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Setter;

import java.time.LocalDateTime;

public record StageInvitationDto(
        Long id,
        @NotNull(message = "Stage ID cannot be null")
        @Positive(message = "Stage ID must be positive")
        Long stageId,
        @NotNull(message = "Author ID cannot be null")
        @Positive(message = "Author ID must be positive")
        Long authorId,
        @NotNull(message = "Invited ID cannot be null")
        @Positive(message = "Invited ID must be positive")
        Long invitedId,
        @NotNull(message = "Status cannot be null")
        StageInvitationStatus status,
        @Size(max = 255, message = "Reason must not exceed 255 characters")
        String reason,
        LocalDateTime createdAt
) {
    public StageInvitationDto {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}