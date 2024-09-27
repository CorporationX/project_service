package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotBlank;

public record DeclineInvitationDto(
        @NotBlank(message = "Reason cannot be blank")
        String reason
) {}