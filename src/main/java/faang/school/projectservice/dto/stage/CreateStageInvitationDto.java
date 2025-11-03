package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateStageInvitationDto(
        @NotNull(message = "Stage ID cannot be null")
        Long stageId,

        @NotNull(message = "Author user ID cannot be null")
        Long authorUserId,

        @NotNull(message = "Invited user ID cannot be null")
        Long invitedUserId,

        String description) {
}
