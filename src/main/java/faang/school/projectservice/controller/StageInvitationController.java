package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stage-invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final Validator validator;

    @PostMapping
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        validator.validateStageInvitationDto(stageInvitationDto);
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PostMapping
    public StageInvitationDto acceptInvitation(@RequestBody StageInvitationDto stageInvitationDto,
                                               @RequestBody TeamMember invited) {
        validator.validateStageInvitationDto(stageInvitationDto);
        return stageInvitationService.acceptInvitation(stageInvitationDto, invited);
    }

    @PostMapping
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto,
                                               @RequestBody TeamMember invited,
                                               @RequestParam("rejectionReason") String rejectionReason) {
        validator.validateStageInvitationDto(stageInvitationDto);
        return stageInvitationService.rejectInvitation(stageInvitationDto, invited, rejectionReason);
    }

    @GetMapping("/members/{teamMemberId}/invitations")
    public List<StageInvitationDto> viewAllInvitationsForSingleMemberWithFilters(
            @PathVariable("teamMemberId") Long teamMemberId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "authorId", required = false) Long authorId) {

        validator.validateTeamMemberId(teamMemberId);
        if (authorId != null) {
            validator.validateAuthorId(authorId);
        }

        return stageInvitationService.getInvitationsForTeamMemberWithFilters(teamMemberId, status, authorId);
    }
}