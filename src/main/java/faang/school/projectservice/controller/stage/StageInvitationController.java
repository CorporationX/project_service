package faang.school.projectservice.controller.stage;

import faang.school.projectservice.model.dto.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class StageInvitationController {

    private final StageInvitationService invitationService;
    public StageInvitationController(StageInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/send")
    public ResponseEntity<StageInvitationDto> sendInvitation(@RequestBody StageInvitationDto invitationDto) {
        if (invitationDto.getStageId() == null || invitationDto.getInviteeId() == null || invitationDto.getAuthorId() == null) {
            return ResponseEntity.badRequest().build();
        }

        StageInvitationDto savedInvitation = invitationService.sendInvitation(invitationDto);
        return ResponseEntity.ok(savedInvitation);
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<StageInvitationDto> acceptInvitation(@PathVariable Long invitationId) {
        StageInvitationDto updatedInvitation = invitationService.acceptInvitation(invitationId);
        return ResponseEntity.ok(updatedInvitation);
    }
     @PostMapping("/{invitationId}/decline")
    public ResponseEntity<StageInvitationDto> declineInvitation(@PathVariable Long invitationId, @RequestBody String reason) {
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
         StageInvitationDto updatedInvitation = invitationService.declineInvitation(invitationId, reason);
        return ResponseEntity.ok(updatedInvitation);
    }
     @GetMapping("/user/{userId}")
    public ResponseEntity<List<StageInvitationDto>> getInvitations(@PathVariable Long userId) {
        List<StageInvitationDto> invitations = invitationService.getInvitationsByUser(userId);
        return ResponseEntity.ok(invitations);
    }
}