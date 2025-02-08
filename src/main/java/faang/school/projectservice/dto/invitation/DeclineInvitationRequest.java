package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record DeclineInvitationRequest(
        @NotNull Long id,
        @NotBlank @Size(max = 255) String description
) {
}
