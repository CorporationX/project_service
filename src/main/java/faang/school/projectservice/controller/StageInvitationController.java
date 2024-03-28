package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping("/sendInvitation")
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PostMapping("/acceptInvitation")
    public void acceptInvitation(Long invitationId, Long invitedId) {
        stageInvitationService.acceptInvitation(invitationId, invitedId);
    }

    @PostMapping("/declineInvitation")
    public void  declineInvitation(Long invitationId, Long invitedId, String description) {
        if (description.isEmpty() || description.isBlank()) {
            throw new DataValidationException("You can not decline invitation without cause!");
        }
        stageInvitationService.declineInvitation(invitationId, invitedId, description);
    }

    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(Long teamMemberId, StageInvitationStatus status) {
        return stageInvitationService.getAllInvitationsForUserWithStatus(teamMemberId, status);
    }
}
