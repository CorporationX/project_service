package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;

public record RejectStageInvitationDto(
        @NotBlank(message = "Rejection reason cannot be blank")
        String reason) {
}
