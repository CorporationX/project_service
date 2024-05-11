package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validation.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator controllerValidation;

    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        controllerValidation.createValidationController(stageInvitationDto);
        return stageInvitationService.createInvitation(stageInvitationDto);
    }

    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        controllerValidation.acceptInvitationValidationController(stageInvitationDto);
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto){
        controllerValidation.rejectInvitationValidationController(stageInvitationDto);
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    public List<StageInvitationDto> showAllInvitationForMember(Long userId, InvitationFilterDto invitationFilterDto) {
        controllerValidation.showAllInvitationForMemberValidationController(userId, invitationFilterDto);
        return stageInvitationService.showAllInvitationForMember(userId, invitationFilterDto);
    }
}
