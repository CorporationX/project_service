package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.service.stage.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/stageInvitation")
@RestController
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto requestStageInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    @PutMapping("/accept/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StageInvitationDto acceptStageInvitation(@RequestBody Long id) {
        return stageInvitationService.acceptStageInvitation(id);
    }

    @PutMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StageInvitationDto rejectStageInvitation(@PathVariable Long id, @RequestBody String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(id, rejectionReason);
    }

    @GetMapping("/show/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageInvitationDto> showMemberStageInvitations(@PathVariable Long id, @RequestBody StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationService.getMemberStageInvitations(id, stageInvitationFilterDto);
    }
}
