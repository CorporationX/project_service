package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationDto sendInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PutMapping("/accept")
    public StageInvitationDto acceptInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    @PutMapping("/reject")
    public StageInvitationDto rejectInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    @GetMapping("/{userId}")
    public List<StageInvitationDto> getInvitationsForTeamMemberWithFilters(
            @PathVariable @Valid @Min(0) Long userId,
            @Valid StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationService.getInvitationsForTeamMemberWithFilters(userId, stageInvitationFilterDto);
    }
}