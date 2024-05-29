package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.RejectionDTO;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;

import java.util.List;

public interface StageInvitationService {
    StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto);

    StageInvitationDto acceptInvitation(Long invitationId);

    StageInvitationDto rejectInvitationWithReason(Long invitationId, RejectionDTO rejectionReason);

    List<StageInvitationDto> getInvitationsWithFilters(StageInvitationFilterDTO stageInvitationFilterDTO);
}
