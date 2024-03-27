package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public StageInvitationDto sendInvitation(Long stageId, Long authorId, Long invitedId, String description) {
        return stageInvitationService.sendInvitation(stageId, authorId, invitedId, description);
    }

    public void acceptInvitation(Long invitationId, Long invitedId) {
        stageInvitationService.acceptInvitation(invitationId, invitedId);
    }

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
