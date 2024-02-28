package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/stage/invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    private final UserContext userContext;

    @PostMapping
    public StageInvitationDto create(@NotNull @Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    @PutMapping("accept/{stageInvitationId}")
    public StageInvitationDto accept(@Positive @PathVariable long stageInvitationId) {
        return stageInvitationService.accept(stageInvitationId);
    }

    @PutMapping("reject/{stageInvitationId}")
    public StageInvitationDto reject(@Positive @PathVariable long stageInvitationId, @NotBlank String message) {
        return stageInvitationService.reject(stageInvitationId, message);
    }

    @PostMapping("/filter")
    public List<StageInvitationDto> findAllInviteByFilter(@NotNull @RequestBody StageInvitationFilterDto filterDto) {
        return stageInvitationService.findAllInviteByFilter(filterDto, userContext.getUserId());
    }
}
