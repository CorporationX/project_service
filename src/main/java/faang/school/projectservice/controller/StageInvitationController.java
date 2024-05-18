package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator stageInvitationValidator;

    @PostMapping("/createInvitation")
    public StageInvitationDto createInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.createValidationController(stageInvitationDto);
        return stageInvitationService.createInvitation(stageInvitationDto);
    }

    @PutMapping("/acceptInvitation")
    public StageInvitationDto acceptInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.acceptInvitationValidationController(stageInvitationDto);
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    @PutMapping("/rejectInvitation")
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto){
        stageInvitationValidator.rejectInvitationValidationController(stageInvitationDto);
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    @GetMapping("/showAllInvitationForMember/{userId}")
    public List<StageInvitationDto> showAllInvitationForMember(@PathVariable Long userId, @RequestBody InvitationFilterDto invitationFilterDto) {
        stageInvitationValidator.showAllInvitationForMemberValidationController(userId, invitationFilterDto);
        return stageInvitationService.showAllInvitationForMember(userId, invitationFilterDto);
    }
}
