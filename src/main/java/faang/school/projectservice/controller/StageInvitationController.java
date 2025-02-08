package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.*;
import faang.school.projectservice.service.invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send")
    public ResponseEntity<SendInvitationResponse> sendInvitation(@RequestBody SendInvitationRequest request) {
        SendInvitationResponse response = stageInvitationService.sendInvitation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<AcceptInvitationResponse> acceptInvitation(@RequestBody AcceptInvitationRequest request) {
        AcceptInvitationResponse response = stageInvitationService.acceptInvitation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decline")
    public ResponseEntity<DeclineInvitationResponse> declineInvitation(@RequestBody DeclineInvitationRequest request) {
        DeclineInvitationResponse response = stageInvitationService.declineInvitation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<InvitationDto>> getFilteredInvitations(@RequestBody InvitationDto filter) {
        List<InvitationDto> filteredInvitations = stageInvitationService.getFilteredInvitations(filter);
        return ResponseEntity.ok(filteredInvitations);
    }
}
