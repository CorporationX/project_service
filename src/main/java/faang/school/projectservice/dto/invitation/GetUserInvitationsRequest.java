package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetUserInvitationsRequest(
        @NotNull Long invited
) {
}
