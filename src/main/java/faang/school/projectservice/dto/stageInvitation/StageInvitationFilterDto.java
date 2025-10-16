package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;

public record StageInvitationFilterDto(
        @NotNull Long teamMemberId,
        StageInvitationStatus status,
        Long stageId
) {
}