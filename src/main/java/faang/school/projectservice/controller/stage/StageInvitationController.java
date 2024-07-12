package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.client.StageInvitationFilterDto;
import faang.school.projectservice.service.stage.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public StageInvitationDto requestStageInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    public StageInvitationDto acceptStageInvitation(Long id) {
        return stageInvitationService.acceptStageInvitation(id);
    }

    public StageInvitationDto rejectStageInvitation(Long id, String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(id, rejectionReason);
    }

    public List<StageInvitationDto> showMemberStageInvitations(Long id, StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationService.getMemberStageInvitations(id, stageInvitationFilterDto);
    }
}
