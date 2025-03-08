package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stageinvitation.ChangeStatusDto;
import faang.school.projectservice.dto.stageinvitation.RejectInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/invitations")
@RestController
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationDto createStageInvitation(@Valid @RequestBody StageInvitationDto dto) {
        return stageInvitationService.createStageInvitation(dto);
    }

    @PutMapping("/updated")
    public StageInvitationUpdateDto updateStageInvitation(@Valid @RequestBody StageInvitationUpdateDto dto) {
        return stageInvitationService.updateStageInvitation(dto);
    }

    @PutMapping("/rejected")
    public RejectInvitationDto rejectStageInvitation(@Valid @RequestBody RejectInvitationDto dto) {
        return stageInvitationService.rejectStageInvitation(dto);
    }

    @PutMapping("/accepted")
    public ChangeStatusDto acceptStageInvitation(@Valid @RequestBody ChangeStatusDto dto) {
        return stageInvitationService.acceptStageInvitation(dto);
    }

    @GetMapping("/member/{invitedId}")
    public List<StageInvitationDto> getStageInvitationForTeamMember(@PathVariable Long invitedId) {
        return stageInvitationService.getStageInvitationForTeamMember(invitedId);
    }
}
