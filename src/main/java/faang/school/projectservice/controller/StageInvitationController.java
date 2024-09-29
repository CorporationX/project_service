package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invitations")
public class StageInvitationController {

    private final StageInvitationService service;

    public StageInvitationController(StageInvitationService service) {
        this.service = service;
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<StageInvitationDto> acceptInvitation(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(service.acceptInvitation(id, userId));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<StageInvitationDto> declineInvitation(@PathVariable Long id, @RequestParam Long userId, @RequestParam String reason) {
        return ResponseEntity.ok(service.declineInvitation(id, userId, reason));
    }
}
