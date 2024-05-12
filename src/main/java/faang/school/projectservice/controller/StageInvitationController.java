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
    private final StageInvitationValidator stageInvitationValidator;

    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.createValidationController(stageInvitationDto);
        return stageInvitationService.createInvitation(stageInvitationDto);
    }

    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.acceptInvitationValidationController(stageInvitationDto);
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    public StageInvitationDto rejectInvitation(String explanation, StageInvitationDto stageInvitationDto){
        stageInvitationValidator.rejectInvitationValidationController(explanation, stageInvitationDto);
        return stageInvitationService.rejectInvitation(explanation, stageInvitationDto);
    }

    public List<StageInvitationDto> showAllInvitationForMember(Long userId, InvitationFilterDto invitationFilterDto) {
        stageInvitationValidator.showAllInvitationForMemberValidationController(userId, invitationFilterDto);
        return stageInvitationService.showAllInvitationForMember(userId, invitationFilterDto);
    }
}
