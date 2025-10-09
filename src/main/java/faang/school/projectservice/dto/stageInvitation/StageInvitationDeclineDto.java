package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StageInvitationDeclineDto(
        @NotNull Long stageInvitationId,
        @NotNull @NotBlank String description
) {
}