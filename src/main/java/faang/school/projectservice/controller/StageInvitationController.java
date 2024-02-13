package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage/invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final StageInvitationValidator validator;

    @PostMapping
    public StageInvitationDto create(@RequestBody StageInvitationDto stageInvitationDto) {
        validator.validateDto(stageInvitationDto);
        return stageInvitationService.create(stageInvitationDto);
    }

    @PutMapping("accept/{stageInvitationId}")
    public StageInvitationDto accept(@PathVariable long stageInvitationId) {
        validator.validateInvitationId(stageInvitationId);
        return stageInvitationService.accept(stageInvitationId);
    }

    @PutMapping("reject/{stageInvitationId}")
    public StageInvitationDto reject(@PathVariable long stageInvitationId, String message) {
        validator.validateReject(stageInvitationId, message);
        return stageInvitationService.reject(stageInvitationId, message);
    }

    @PostMapping("/filter")
    public List<StageInvitationDto> findAllInviteByFilter(@RequestBody StageInvitationFilterDto filterDto,
                                                          @RequestHeader("user-id") String userId) {
        validator.validateFilter(filterDto);
        return stageInvitationService.findAllInviteByFilter(filterDto, userId);
    }
}
