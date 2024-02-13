package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.ValidateStageInvitationException;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stageInvitation")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    private void validate(Object field, String message) {
        if (field == null) throw new ValidateStageInvitationException(message);
    }

    @PostMapping("/create")
    public StageInvitationDto createInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        validate(stageInvitationDto.getStage(), "Stage missing from request");
        validate(stageInvitationDto.getAuthor(), "Author missing from request");
        validate(stageInvitationDto.getInvited(), "Invited missing from request");
        return stageInvitationService.create(stageInvitationDto);
    }

    @PostMapping("/accept")
    public void acceptInvitation(@PathVariable("userId") Long userId,
                                 @PathVariable("invitationId") Long invitationId) {
        validate(userId, "User missing from request");
        validate(invitationId, "Invited missing from request");
        stageInvitationService.accept(userId, invitationId);
    }

    @PostMapping("/reject")
    public void rejectInvitation(@PathVariable("userId") Long userId,
                                 @PathVariable("invitationId") Long invitationId,
                                 @PathVariable("description") String description) {
        validate(userId, "User missing from request");
        validate(invitationId, "Invited missing from request");
        validate(description, "Description missing from request");
        stageInvitationService.reject(userId, invitationId, description);
    }

    @GetMapping("/getAll/{stageInvitationId}")
    public List<StageInvitationDto> getAllInvitation(@PathVariable("stageInvitationId") Long id) {
        validate(id, "Incorrect user id");
        return stageInvitationService.getAll(id);
    }


}
