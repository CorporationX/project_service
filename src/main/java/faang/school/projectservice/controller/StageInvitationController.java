package faang.school.projectservice.controller;

import faang.school.projectservice.dto.FilterDto.StageInvitationFilterDto;
import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;


    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    public void acceptInvitation(long invitedId) {
        stageInvitationService.acceptInvitation(invitedId);
    }

    public void rejectStageInvitation(Long id, String rejectionReason) {
        stageInvitationService.rejectStageInvitation(id, rejectionReason);
    }

    public void getAllInvitationsForOneParticipant(Long participantId, StageInvitationFilterDto filter) {
        stageInvitationService.getAllInvitationsForOneParticipant(participantId, filter);
    }
}
