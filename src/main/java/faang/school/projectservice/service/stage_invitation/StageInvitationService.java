package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;

import java.util.List;

public interface StageInvitationService {

    StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto);

    StageInvitationDto acceptInvitation(long userId, long inviteId);

    StageInvitationDto rejectInvitation(long userId, long inviteId, String reason);

    List<StageInvitationDto> getInvitationsWithFilters(StageInvitationFilterDto stageInvitationFilterDto);
}
