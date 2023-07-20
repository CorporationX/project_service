package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stage-invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationDto sendInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PostMapping
    public StageInvitationDto acceptInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto,
                                               @RequestBody TeamMember invited) {
        return stageInvitationService.acceptInvitation(stageInvitationDto, invited);
    }

    @PostMapping
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto,
                                               @RequestBody TeamMember invited,
                                               @RequestParam("rejectionReason") String rejectionReason){
        return stageInvitationService.rejectInvitation(stageInvitationDto, invited, rejectionReason);
    }



    @GetMapping("/members/{teamMemberId}/invitations")
    public List<StageInvitationDto> viewAllInvitationsForSingleMemberWithFilters(
            @PathVariable("teamMemberId") Long teamMemberId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "authorId", required = false) Long authorId) {

        return stageInvitationService.getInvitationsForTeamMemberWithFilters(teamMemberId, status, authorId);
    }
}
