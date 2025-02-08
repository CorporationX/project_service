package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InvitationDto(
        @NotNull Long id,
        @NotNull Long stageId,
        @NotNull Long author,
        @NotNull Long invited,
        @Size(max = 255) String description,
        @Size(max = 20) String status
) {
}
