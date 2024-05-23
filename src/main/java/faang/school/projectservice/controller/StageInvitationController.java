package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage_invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator stageInvitationValidator;

    @PostMapping
    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.sendInvite(stageInvitationDto);
    }

    @PutMapping("/accepted")
    public StageInvitationDto acceptInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    @PutMapping("/rejected")
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    @GetMapping("/all/{user_id}")
    public List<StageInvitationDto> getAllInvitationsForOneUser(@PathVariable("user_id") long userId,
                                                                StageInvitationFilterDto stageInvitationFilterDto) {
        stageInvitationValidator.validateId(userId);
        return stageInvitationService.getAllInvitationsForUserWithStatus(userId, stageInvitationFilterDto);
    }
}