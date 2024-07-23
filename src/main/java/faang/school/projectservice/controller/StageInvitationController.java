package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/stageInvitation")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send")
    public StageInvitationDto sendAnInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto == null) {
            log.error("stageInvitationDto is null");
            throw new IllegalArgumentException("stageInvitationDto is null");
        }
        return stageInvitationService.sendAnInvitation(stageInvitationDto);
    }

    @PostMapping("/accept/{userId}")
    public StageInvitationDto acceptInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto,
                                               @PathVariable long userId) {
        if (stageInvitationDto == null) {
            log.error("stageInvitationDto is null");
            throw new IllegalArgumentException("stageInvitationDto is null");
        }
        return stageInvitationService.acceptInvatation(stageInvitationDto, userId);
    }

    @PostMapping("/decline")
    public StageInvitationDto declineTheInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto == null) {
            log.error("stageInvitationDto is null");
            throw new IllegalArgumentException("stageInvitationDto is null");
        }

        if (stageInvitationDto.getDescription() == null || stageInvitationDto.getDescription().isEmpty()) {
            log.error("description of stageInvitation is null");
            throw new NullPointerException("description of stageInvitation is null");
        }
        return stageInvitationService.declineTheInvitation(stageInvitationDto);
    }

    @PostMapping("/stageUser/{userId}")
    public List<StageInvitationDto> getStageInvitationForUser(@Valid @RequestBody StageInvitationFilterDto stageInvitationFilterDto,
                                                              @PathVariable long userId) {
        if (stageInvitationFilterDto == null) {
            log.error("stageInvitationFilterDto is null");
            throw new IllegalArgumentException("stageInvitationFilterDto is null");
        }

        if (userId < 0) {
            log.error("userId < 0");
            throw new IllegalArgumentException("userId < 0");
        }
        return stageInvitationService.getStageInvitationForUser(stageInvitationFilterDto, userId);
    }
}