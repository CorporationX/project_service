package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;

import java.util.List;

public interface StageInvitationService {
    StageInvitationDto sendInvitation(StageInvitationDto dto);
    StageInvitationDto acceptInvitation(Long invitationId, Long userId);
    StageInvitationDto declineInvitation(Long invitationId, Long userId, String reason);
    List<StageInvitationDto> getUserInvitations(Long userId);
}