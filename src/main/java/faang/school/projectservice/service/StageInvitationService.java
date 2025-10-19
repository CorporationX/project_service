package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.CreateStageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

import java.util.List;


public interface StageInvitationService {

    StageInvitationDto sendInvitation(CreateStageInvitationDto stageInvitationDto);

    StageInvitationDto acceptInvitation(Long invitationId);

    StageInvitationDto rejectInvitation(Long invitationId, String reason);

    List<StageInvitationDto> getInvitationsForUser(Long userId, StageInvitationStatus status);
}
