package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;

import java.util.List;

public interface StageInvitationService {
    void sendInvitation(StageInvitationDto stageInvitationDto);

    void acceptInvitation(Long invitationId);

    void rejectInvitationWithReason(Long invitationId, String rejectionReason);

    List<StageInvitationDto> getInvitationsWithFilters(StageInvitationFilterDTO stageInvitationFilterDTO);
}
