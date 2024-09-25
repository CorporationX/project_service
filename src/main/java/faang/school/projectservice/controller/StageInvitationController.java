package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.DeclineInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import faang.school.projectservice.validation.CreateGroup;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PutMapping("/{id}/decline")
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