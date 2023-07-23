package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import lombok.Data;
import org.springframework.stereotype.Controller;

@Data
@Controller
public class StageInvitationController {
    private final StageInvitationService service;

    public StageInvitationDto create(StageInvitationDto invitationDto) {
        validate(invitationDto);
        return service.create(invitationDto);
    }

    private void validate(StageInvitationDto invitationDto) {
        if (invitationDto == null ||
                invitationDto.getStageId() == null ||
                invitationDto.getAuthorId() == null ||
                invitationDto.getInvitedId() == null ||
                invitationDto.getAuthorId() == invitationDto.getInvitedId()) {

            throw new DataValidationException("StageInvitation is invalid");
        }
    }

    public StageInvitationDto accept(long invitationId){
        return service.accept(invitationId);
    }
}
