package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    public StageInvitationDto acceptInvitation(long invitedId) {
        return stageInvitationService.acceptInvitation(invitedId);
    }

    public StageInvitationDto rejectStageInvitation(Long id, String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(id, rejectionReason);
    }

    public List<StageInvitationDto> viewAllInvitation(Long participantId,
                                                      StageInvitationFilterDto filter) {
        return stageInvitationService.viewAllInvitation(participantId, filter);
    }
}
