package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.DeclineInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.validation.CreateGroup;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stage-invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @Operation(summary = "Send a new stage invitation")
    @PostMapping
    public StageInvitationDto sendInvitation(
            @Validated(CreateGroup.class) @RequestBody StageInvitationDto invitationDto) {
        return stageInvitationService.sendInvitation(invitationDto);
    }

    @Operation(summary = "Accept a stage invitation")
    @PostMapping("/{id}/accept")
    public StageInvitationDto acceptInvitation(@PathVariable @Positive Long id) {
        return stageInvitationService.acceptInvitation(id);
    }

    @Operation(summary = "Decline a stage invitation")
    @PostMapping("/{id}/decline")
    public StageInvitationDto declineInvitation(
            @PathVariable @Positive Long id, @Valid @RequestBody DeclineInvitationDto declineInvitationDto) {
        return stageInvitationService.declineInvitation(id, declineInvitationDto.getReason());
    }

    @Operation(summary = "Get invitations with filters")
    @GetMapping
    public List<StageInvitationDto> getInvitations(@Valid StageInvitationFilterDto filterDto) {
        return stageInvitationService.getInvitations(filterDto);
    }
}