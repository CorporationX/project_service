package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.Positive;

public record StageInvitationFilterDto(
        @Positive(message = "Invited ID must be positive")
        Long invitedId,
        StageInvitationStatus status
) {}