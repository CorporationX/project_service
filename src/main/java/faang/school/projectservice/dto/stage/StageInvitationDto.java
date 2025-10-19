package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record StageInvitationDto(
        Long id,

        String description,

        @NotNull(message = "Status cannot be null")
        StageInvitationStatus status,

        @NotNull(message = "Stage ID cannot be null")
        Long stageId,

        @NotNull(message = "Author user ID cannot be null")
        Long authorUserId,

        @NotNull(message = "Invited user ID cannot be null")
        Long invitedUserId,

        String authorName,
        String invitedUserName,
        String stageName
) {}
