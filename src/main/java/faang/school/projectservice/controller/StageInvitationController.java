package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.zip.DataFormatException;

@RestController
@AllArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public StageInvitationDto sendInvitation(Long stageId, Long authorId, Long invitedId, String description) {
        if (description.isEmpty()) {
            throw new DataFormatException("Description can not be empty!");
        }
        stageInvitationService.sendInvitation(stageId, authorId, invitedId, description);
    }
}
