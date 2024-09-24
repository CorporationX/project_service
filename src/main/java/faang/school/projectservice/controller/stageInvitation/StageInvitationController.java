package faang.school.projectservice.controller.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.stageInvitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1/stageInvitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.createInvitation(stageInvitationDto);
    }

    @PatchMapping("/a/{id}")
    public StageInvitationDto acceptInvitation(@PathVariable long id) {
        return stageInvitationService.acceptInvitation(id);
    }

    @PutMapping
    public StageInvitationDto rejectInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    @GetMapping("/f/{invitedId}")
    public List<StageInvitationDto> getInvitations(@PathVariable long invitedId,
                                                   @RequestParam(value = "invitedStageName", required = false)
                                                   String invitedStageName,
                                                   @RequestParam(value = "status", required = false)
                                                   StageInvitationStatus status) {
        StageInvitationFilterDto filter = new StageInvitationFilterDto(invitedId, invitedStageName, status);
        return stageInvitationService.getInvitations(filter);
    }
}

