package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/send-invitations")
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PutMapping("/accept-invitations/{invitationId}")
    public StageInvitationDto acceptStageInvitation(@PathVariable long invitationId) {
        return stageInvitationService.acceptStageInvitation(invitationId);
    }

    @PutMapping("/reject-invitations/{id}")
    public StageInvitationDto rejectStageInvitation(@PathVariable("id") Long id, @RequestBody String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(id, rejectionReason);
    }

    @GetMapping("/list-invitations/{participantId}")
    public List<StageInvitationDto> viewAllInvitation(@PathVariable Long participantId,
                                                      @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.viewAllInvitation(participantId, filter);
    }
}
