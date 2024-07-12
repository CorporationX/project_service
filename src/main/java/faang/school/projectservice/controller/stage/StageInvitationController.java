package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.client.StageInvitationFilterDto;
import faang.school.projectservice.service.stage.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/stageInvitation")
@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto requestStageInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    @PutMapping("/accept/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StageInvitationDto acceptStageInvitation(Long id) {
        return stageInvitationService.acceptStageInvitation(id);
    }

    @PutMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StageInvitationDto rejectStageInvitation(Long id, String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(id, rejectionReason);
    }

    @PutMapping("/show/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageInvitationDto> showMemberStageInvitations(Long id, StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationService.getMemberStageInvitations(id, stageInvitationFilterDto);
    }
}
