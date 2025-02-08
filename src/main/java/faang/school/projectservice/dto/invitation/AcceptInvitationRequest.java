package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AcceptInvitationRequest(
        @NotNull Long id
) {}
