package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StageInvitationController {

    private StageInvitationService stageInvitationService;

    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto == null) {
            log.error("stageInvitationDto is null");
            throw new IllegalArgumentException("stageInvitationDto is null");
        }
        return stageInvitationService.sendAnInvitation(stageInvitationDto);
    }

    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto, long userId) {
        if (stageInvitationDto == null) {
            log.error("stageInvitationDto is null");
            throw new IllegalArgumentException("stageInvitationDto is null");
        }
        return stageInvitationService.acceptInvatation(stageInvitationDto, userId);
    }

    public StageInvitationDto declineTheInvitation(StageInvitationDto stageInvitationDto) {
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

    public List<StageInvitationDto> getStageInvitationForUser(StageInvitationFilterDto stageInvitationFilterDto,
                                                       long userId) {
        return stageInvitationService.getStageInvitationForUser(stageInvitationFilterDto, userId);
    }
}
