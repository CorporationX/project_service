package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import lombok.Data;
import org.springframework.stereotype.Controller;

import java.util.List;

@Data
@Controller
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.createInvitation(stageInvitationDto);
    }

    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.acceptInvitation(stageInvitationDto);
    }

    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto) {
        return stageInvitationService.rejectInvitation(stageInvitationDto);
    }

    public List<StageInvitationDto> viewAllInvitation(StageInvitationDto stageInvitationDto, StageInvitationFilterDto filter) {
        return stageInvitationService.viewAllInvitation(stageInvitationDto, filter);
    }
}
