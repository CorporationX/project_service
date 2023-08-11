package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
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

    public StageInvitationDto accept(long invitationId) {
        return service.accept(invitationId);
    }

    public StageInvitationDto reject(long invitationId, String message){
        if (message == null || message.isBlank()){
            throw new DataValidationException("Rejection must contains message");
        }
        return service.reject(invitationId, message);
    }

    public List<StageInvitationDto> getStageInvitationsWithFilters(StageInvitationFilterDto filterDto) {
        return service.getStageInvitationsWithFilters(filterDto);
    }
}
