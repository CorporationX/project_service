package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator validator;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        validator.validateDto(stageInvitationDto);
        return stageInvitationService.create(stageInvitationDto);
    }

    public StageInvitationDto accept(long stageInvitationId) {
        validator.validateInvitationId(stageInvitationId);
        return stageInvitationService.accept(stageInvitationId);
    }

    public StageInvitationDto reject(long stageInvitationId, String message) {
        validator.validateReject(stageInvitationId, message);
        return stageInvitationService.reject(stageInvitationId, message);
    }

    public List<StageInvitationDto> findAllInviteByFilter(StageInvitationFilterDto filterDto) {
        validator.validateFilter(filterDto);
        return stageInvitationService.findAllInviteByFilter(filterDto);
    }

}
