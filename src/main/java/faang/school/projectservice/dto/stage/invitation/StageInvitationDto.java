package faang.school.projectservice.dto.stage.invitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record StageInvitationDto(
        Long id,
        @Positive @NotNull Long stageId,
        @Positive @NotNull Long authorId,
        @Positive @NotNull Long invitedId
) {
}
