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
        return stageInvitationService.sendInvitation(createStageInvitationDto);
    }

    @PutMapping("/{invitationId}/accept")
    public StageInvitationDto acceptInvitation(@PathVariable Long invitationId) {
        return stageInvitationService.acceptInvitation(invitationId);
    }

    @PutMapping("/{invitationId}/reject")
    public StageInvitationDto rejectInvitation(
            @PathVariable Long invitationId,
            @Valid @RequestBody String reason) {
        return stageInvitationService.rejectInvitation(invitationId, reason);
    }

    @GetMapping
    public List<StageInvitationDto> getInvitationsForUser(
            @RequestParam Long userId,
            @RequestParam(required = false) StageInvitationStatus status) {
        return stageInvitationService.getInvitationsForUser(userId, status);
    }
}
