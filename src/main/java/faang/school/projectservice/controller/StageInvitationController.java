package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/invitations")
    public ResponseEntity<StageInvitationDto> sendInvitation(@RequestBody StageInvitationDto invitationDto) {
        return ResponseEntity.ok(stageInvitationService.sendInvitation(invitationDto));
    }

    @PutMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<StageInvitationDto> acceptInvitation(@PathVariable long invitationId) {
        return ResponseEntity.ok(stageInvitationService.acceptInvitation(invitationId));
    }

    @PutMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<StageInvitationDto> rejectInvitation(@PathVariable long invitationId,
                                                               @RequestParam String reason) {
        return ResponseEntity.ok(stageInvitationService.rejectInvitation(invitationId, reason));
    }

    @GetMapping("/invitations/{invitationId}")
    public ResponseEntity <List<StageInvitationDto>> getAllInvitations(@PathVariable long invitationId) {
        return ResponseEntity.ok(stageInvitationService.getAllInvitations(invitationId));
    }

}