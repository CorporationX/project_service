package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AcceptInvitationResponse(
        @NotNull Long id,
        @Size(max = 20) String status
) {}

