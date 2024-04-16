package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping("/send")
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PostMapping("/{invitationId}/accept")
    public void acceptInvitation(@RequestParam("invitationId") Long invitationId) {
        stageInvitationService.acceptInvitation(invitationId);
    }

    @PostMapping("/{invitationId}/decline")
    public void  declineInvitation(@RequestParam("invitationId") Long invitationId,
                                   @RequestBody String reason) {
        if (reason.isEmpty() || reason.isBlank()) {
            throw new DataValidationException("You can not decline invitation without cause!");
        }
        stageInvitationService.declineInvitation(invitationId, reason);
    }

    @PostMapping("/getAllInvitations")
    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(@RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getAllInvitationsForUserWithStatus(filter);
    }
}
