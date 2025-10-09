package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;

public record StageInvitationFilterDto(
        @NotNull Long teamMemberId,
        @NotNull StageInvitationStatus status,
        @NotNull Long stageId
) {
}