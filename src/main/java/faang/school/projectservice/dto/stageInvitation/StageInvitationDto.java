package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

public record StageInvitationDto(
        String description,
        StageInvitationStatus status,
        Long stageId,
        Long invitedId
) {
}