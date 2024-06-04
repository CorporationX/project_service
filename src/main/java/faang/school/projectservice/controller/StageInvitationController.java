package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stages/invitations")
@RequiredArgsConstructor
@Tag(name = "StageInvitation")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;
    private final UserContext userContext;

    @Operation(summary = "Send new invite")
    @PostMapping()
    public StageInvitationDto sendInvite(@Valid @RequestBody StageInvitationCreateDto stageInvitationCreateDto) {
        stageInvitationCreateDto.setAuthorId(userContext.getUserId());
        return stageInvitationService.sendInvitation(stageInvitationCreateDto);
    }

    @Operation(summary = "Accept invite")
    @PostMapping("/accept/{inviteId}")
    public StageInvitationDto acceptInvite(@PathVariable long inviteId) {
        long userId = userContext.getUserId();
        return stageInvitationService.acceptInvitation(userId, inviteId);
    }

    @Operation(summary = "Reject invite")
    @PostMapping("/reject/{inviteId}")
    public StageInvitationDto rejectInvite(@PathVariable long inviteId, @RequestParam String reason) {
        long userId = userContext.getUserId();
        return stageInvitationService.rejectInvitation(userId, inviteId, reason);
    }

    @Operation(summary = "Get invites with filters")
    @GetMapping()
    public List<StageInvitationDto> getInvites(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationService.getInvitationsWithFilters(stageInvitationFilterDto);
    }
}
