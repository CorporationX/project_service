package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

public record StageInvitationFilterDto(
        Long invitedId,
        StageInvitationStatus status
) {}