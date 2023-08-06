package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validate.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator stageInvitationValidator;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateStageInvitationDto(stageInvitationDto);
        return stageInvitationService.create(stageInvitationDto);
    }
}
