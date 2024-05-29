package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "/stageInvitation")
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PutMapping("acceptInvitation/{invitationId}")
    StageInvitationDto acceptInvitation(@PathVariable Long invitationId) {
        return stageInvitationService.acceptInvitation(invitationId);
    }

    @PutMapping("/rejectInvitation/{invitationId}")
    StageInvitationDto rejectInvitationWithReason(@PathVariable Long invitationId, @RequestParam String rejectionReason) {
        return stageInvitationService.rejectInvitationWithReason(invitationId, rejectionReason);
    }

    @PostMapping("/getWithFilter")
    List<StageInvitationDto> getInvitationsWithFilters(@RequestBody StageInvitationFilterDTO stageInvitationFilterDTO) {
        return stageInvitationService.getInvitationsWithFilters(stageInvitationFilterDTO);
    }
}
