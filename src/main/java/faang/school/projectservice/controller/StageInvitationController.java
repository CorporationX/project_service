package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.CreateStageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto sendInvitation(@Valid @RequestBody CreateStageInvitationDto createStageInvitationDto) {
        log.info("Received request to send stage invitation: inviterId={}, invitedId={}, stageId={}",
                createStageInvitationDto.stageId(),
                createStageInvitationDto.stageId(),
                createStageInvitationDto.stageId());
        StageInvitationDto result = stageInvitationService.sendInvitation(createStageInvitationDto);

        log.info("Stage invitation successfully sent: invitationId={}", result.id());
        return result;
    }

    @PutMapping("/{invitationId}/accept")
    public StageInvitationDto acceptInvitation(@PathVariable Long invitationId) {
        log.info("Received request to accept stage invitation with id={}", invitationId);

        StageInvitationDto result = stageInvitationService.acceptInvitation(invitationId);

        log.info("Stage invitation accepted successfully: invitationId={}, status={}",
                result.id(), result.status());
        return result;
    }

    @PutMapping("/{invitationId}/reject")
    public StageInvitationDto rejectInvitation(
            @PathVariable Long invitationId,
            @Valid @RequestBody String reason) {
        log.info("Received request to reject stage invitation with id={}, reason={}", invitationId, reason);

        StageInvitationDto result = stageInvitationService.rejectInvitation(invitationId, reason);

        log.info("Stage invitation rejected successfully: invitationId={}, status={}",
                result.id(), result.status());
        return result;
    }

    @GetMapping
    public List<StageInvitationDto> getInvitationsForUser(
            @RequestParam Long userId,
            @RequestParam(required = false) StageInvitationStatus status) {
        log.info("Received request to get stage invitations for userId={}, statusFilter={}", userId, status);

        List<StageInvitationDto> invitations = stageInvitationService.getInvitationsForUser(userId, status);

        log.info("Retrieved {} stage invitations for userId={}", invitations.size(), userId);
        return invitations;
    }
}
