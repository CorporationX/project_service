package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.StageInvitationValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage_invitation")
@Tag(name = "Stage Invitation", description = "The Stage Invitation API")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator stageInvitationValidator;

    @Operation(summary = "Send a Invitations", tags = "Invitation")
    @PostMapping
    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.sendInvite(stageInvitationDto);
    }

    @Operation(summary = "Accept a Invitation", tags = "Invitation")
    @PutMapping("/accepted")
    public StageInvitationDto acceptInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    @Operation(summary = "Reject a Invitation", tags = "Invitation")
    @PutMapping("/rejected")
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    @Operation(summary = "Get all Invitations for one user", tags = "Invitation")
    @GetMapping("/all/{user_id}")
    public List<StageInvitationDto> getAllInvitationsForOneUser(@PathVariable("user_id") long userId,
                                                                StageInvitationFilterDto stageInvitationFilterDto) {
        stageInvitationValidator.validateId(userId);
        return stageInvitationService.getAllInvitationsForUserWithStatus(userId, stageInvitationFilterDto);
    }
}