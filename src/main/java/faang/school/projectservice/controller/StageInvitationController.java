package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class StageInvitationController {

    private final StageInvitationService service;

    public StageInvitationController(StageInvitationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<StageInvitationDto> sendInvitation(@RequestBody StageInvitationDto dto) {
        if (dto.getInvitedId() == null || dto.getStageId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.sendInvitation(dto));
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<StageInvitationDto> acceptInvitation(@PathVariable Long id) {
        return ResponseEntity.ok(service.acceptInvitation(id));
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<StageInvitationDto> declineInvitation(@PathVariable Long id, @RequestBody String reason) {
        return ResponseEntity.ok(service.declineInvitation(id, reason));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StageInvitationDto>> getUserInvitations(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserInvitations(userId));
    }
}



