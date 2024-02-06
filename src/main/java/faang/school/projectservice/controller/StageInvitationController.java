package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.ValidateStageInvitationException;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    private void validate(Object field, String message) {
        if (field == null) throw new ValidateStageInvitationException(message);
    }

    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        validate(stageInvitationDto.getStage(), "Stage missing from request");
        validate(stageInvitationDto.getAuthor(), "Author missing from request");
        validate(stageInvitationDto.getInvited(), "Invited missing from request");
        return stageInvitationService.create(stageInvitationDto);
    }

    public void acceptInvitation(Long userId, Long invitationId) {
        validate(userId, "User missing from request");
        validate(invitationId, "Invited missing from request");
        stageInvitationService.accept(userId, invitationId);
    }

    public void rejectInvitation(Long userId, Long invitationId, String description) {
        validate(userId, "User missing from request");
        validate(invitationId, "Invited missing from request");
        validate(description, "Description missing from request");
        stageInvitationService.reject(userId, invitationId, description);
    }

    public List<StageInvitationDto> getAllInvitation(Long id) {
        validate(id, "Incorrect user id");
        return stageInvitationService.getAll(id);
    }


}
