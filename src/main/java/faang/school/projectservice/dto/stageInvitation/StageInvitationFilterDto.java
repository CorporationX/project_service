package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StageInvitationFilterDto(
        @NotNull(message = "Team member not be empty")
        @Positive(message = "Team member not be negative")
        Long teamMemberId,
        StageInvitationStatus status,
        @Positive(message = "Stage cannot be negative")
        Long stageId
) {
}