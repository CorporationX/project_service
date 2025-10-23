package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StageInvitationDeclineDto(
        @NotNull(message = "Invitation cannot be empty")
        Long stageInvitationId,
        @NotBlank(message = "Description cannot be empty")
        @Size(max = 255)
        String description
) {
}