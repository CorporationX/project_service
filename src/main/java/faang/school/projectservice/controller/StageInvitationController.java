package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService service;

    @PostMapping("/send")
    public ResponseEntity<StageInvitationDto> sendInvitation(@Valid @RequestBody StageInvitationDto dto) {
        return ResponseEntity.ok(service.sendInvitation(dto));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
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



