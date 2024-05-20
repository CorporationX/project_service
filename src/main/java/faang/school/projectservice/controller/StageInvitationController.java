package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    void sendInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationService.sendInvitation(stageInvitationDto);
    }

    void acceptInvitation(Long invitationId) {
        stageInvitationService.acceptInvitation(invitationId);
    }

    void rejectInvitationWithReason(Long invitationId, String rejectionReason) {
        stageInvitationService.rejectInvitationWithReason(invitationId, rejectionReason);
    }

    List<StageInvitationDto> getInvitationsWithFilters(StageInvitationFilterDTO stageInvitationFilterDTO) {
        return stageInvitationService.getInvitationsWithFilters(stageInvitationFilterDTO);
    }


}
