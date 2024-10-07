package faang.school.projectservice.dto.stage_inavation;

import jakarta.validation.constraints.NotBlank;

public record DeclineInvitationDto(
        @NotBlank(message = "Reason cannot be empty")
        String reason
) {
}