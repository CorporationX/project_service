package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("/invitation")
@Validated
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
            @PathVariable Long userId,
            StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationService.getInvitationsForTeamMemberWithFilters(userId, stageInvitationFilterDto);
    }
}