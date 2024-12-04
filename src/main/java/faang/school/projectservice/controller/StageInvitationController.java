package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/projects/stage-invitations")
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationDto sendInvitation(@RequestBody @Valid StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PatchMapping("/{stageInvitationId}/accept")
    public StageInvitationDto acceptInvitation(@PathVariable @Positive Long stageInvitationId) {
        return stageInvitationService.acceptInvitation(stageInvitationId);
    }

    @PatchMapping("/{stageInvitationId}/reject")
    public StageInvitationDto rejectInvitation(@PathVariable @Positive Long stageInvitationId,
                                               @RequestParam @NotBlank String reason) {
        return stageInvitationService.rejectInvitation(stageInvitationId, reason);
    }

    @GetMapping
    public List<StageInvitationDto> getUserAllFilteredInvitations(@RequestParam @Positive Long invitedId,
                                                                  @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getAllFilteredInvitations(invitedId, filter);
    }
}
