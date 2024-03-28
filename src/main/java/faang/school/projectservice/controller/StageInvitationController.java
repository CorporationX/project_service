package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/{invitedId}/acceptInvitation")
    public void acceptInvitation(@RequestParam("invitationId") Long invitationId,
                                 @PathVariable("invitedId") Long invitedId) {
        stageInvitationService.acceptInvitation(invitationId, invitedId);
    }

    @PostMapping("/{invitatedId}/declineInvitation")
    public void  declineInvitation(@RequestParam("invitationId") Long invitationId,
                                   @PathVariable("invitatedId")Long invitedId,
                                   @RequestBody String description) {
        if (description.isEmpty() || description.isBlank()) {
            throw new DataValidationException("You can not decline invitation without cause!");
        }
        stageInvitationService.declineInvitation(invitationId, invitedId, description);
    }

    @PostMapping("/{invitedId}/getAllInvitations")
    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(@PathVariable("invitedId") Long teamMemberId,
                                                                       @RequestBody StageInvitationStatus status) {
        return stageInvitationService.getAllInvitationsForUserWithStatus(teamMemberId, status);
    }
}
