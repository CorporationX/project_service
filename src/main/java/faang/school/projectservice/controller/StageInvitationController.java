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
@Tag(name = "Stage Invitation", description = "Operations related to stage invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator stageInvitationValidator;

    @PostMapping
    @Operation(summary = "Send an invitation", description = "Send a new stage invitation")
    public StageInvitationDto sendAnInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.sendInvite(stageInvitationDto);
    }

    @PutMapping("/accepted")
    @Operation(summary = "Accept an invitation", description = "Accept a stage invitation")
    public StageInvitationDto acceptInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    @PutMapping("/rejected")
    @Operation(summary = "Reject an invitation", description = "Reject a stage invitation")
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateId(stageInvitationDto.getId());
        stageInvitationValidator.validateDescription(stageInvitationDto.getDescription());
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    @GetMapping("/all/{user_id}")
    @Operation(summary = "Get all invitations for a user", description = "Retrieve all stage invitations for a specific user")
    public List<StageInvitationDto> getAllInvitationsForOneUser(@PathVariable("user_id") long userId,
                                                                @RequestBody StageInvitationFilterDto stageInvitationFilterDto) {
        stageInvitationValidator.validateId(userId);
        return stageInvitationService.getAllInvitationsForUserWithStatus(userId, stageInvitationFilterDto);
    }
}