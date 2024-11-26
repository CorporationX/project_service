package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StageInvitationController {
    private StageInvitationService stageInvitationService;

    @PostMapping("/new")
    public StageInvitationDto sendAnInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        log.info("A request has been received to send an invitation {}", stageInvitationDto.getDescription());
        return stageInvitationService.sendAnInvitation(stageInvitationDto);
    }

    @PutMapping("/accepted-invitation")
    public StageInvitationDto acceptAnInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        log.info("A request has been received to accept an invitation {}", stageInvitationDto.getDescription());
        return stageInvitationService.acceptAnInvitation(stageInvitationDto);
    }

    @PutMapping("/reject")
    public StageInvitationDto rejectAnInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        log.info("A request has been received to reject an invitation {}", stageInvitationDto.getDescription());
        return stageInvitationService.rejectAnInvitation(stageInvitationDto);
    }

    @GetMapping("/all-invitations")
    public List<StageInvitationDto> viewAllInvitationsForOneParticipant(
            @RequestBody StageInvitationDto stageInvitationDto,
            @RequestBody(required = false) StageInvitationFilterDto filter) {
        log.info("A request has been received to view all invitations with filters for {}", stageInvitationDto.getInvitedId());
        return stageInvitationService.viewAllInvitationsForOneParticipant(stageInvitationDto.getInvitedId(), filter);
    }
}