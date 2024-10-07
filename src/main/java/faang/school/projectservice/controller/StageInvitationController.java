package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_inavation.DeclineInvitationDto;
import faang.school.projectservice.dto.stage_inavation.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService invitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto createInvitation(@Valid @RequestBody StageInvitationDto invitationDto) {
        return invitationService.createInvitation(invitationDto);
    }

    @PatchMapping("/{invitationId}/accept")
    public StageInvitationDto acceptInvitation(@PathVariable Long invitationId) {
        return invitationService.acceptInvitation(invitationId);
    }

    @PatchMapping("/{invitationId}/decline")
    public StageInvitationDto declineInvitation(@PathVariable Long invitationId, @Valid @RequestBody DeclineInvitationDto declineDto) {
        return invitationService.declineInvitation(invitationId, declineDto.reason());
    }

    @GetMapping
    public List<StageInvitationDto> getInvitations(@RequestParam(required = false) Long participantId, @RequestParam(required = false) String status) {
        return invitationService.getInvitations(participantId, status);
    }

    @GetMapping("/{invitationId}")
    public StageInvitationDto getInvitationById(@PathVariable Long invitationId) {
        return invitationService.getInvitationById(invitationId);
    }
}
