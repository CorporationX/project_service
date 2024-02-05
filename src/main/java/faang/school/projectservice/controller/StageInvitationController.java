package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.ValidateStageInvitationException;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    private void validateStageInvitationDto(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStage() == null)
            throw new ValidateStageInvitationException("Stage missing from request");
        if (stageInvitationDto.getAuthor() == null)
            throw new ValidateStageInvitationException("Author missing from request");
        if (stageInvitationDto.getInvited() == null)
            throw new ValidateStageInvitationException("Invited missing from request");
    }

    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        validateStageInvitationDto(stageInvitationDto);
        return stageInvitationService.create(stageInvitationDto);
    }

    public void acceptInvitation() {
        stageInvitationService.accept();
    }

    public void rejectInvitation() {
        stageInvitationService.reject();
    }

    public void filterInvitation() {
        stageInvitationService.filter();
    }

}
