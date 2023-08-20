package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.filter.stageinvite.FilterStageInviteDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/invites")
public class StageInvitationController {
    private final StageInvitationService service;
    private final UserContext userContext;

    @PostMapping
    public StageInvitationDto sendInvite(@Valid StageInvitationDto stageInvitationDto){
        return service.sendInvite(stageInvitationDto, userContext.getUserId());
    }

    @PutMapping("/accept")
    public StageInvitationDto acceptInvite(@Valid StageInvitationDto stageInvitationDto){
        return service.acceptInvite(stageInvitationDto, userContext.getUserId());
    }

    @PutMapping("/reject")
    public StageInvitationDto rejectInvite(@Valid StageInvitationDto stageInvitationDto){
        return service.rejectInvite(stageInvitationDto, userContext.getUserId());
    }

    @GetMapping("/{userId}")
    public List<StageInvitationDto> getFilteredInvites(@NotNull @PathVariable Long userId,
                                                       FilterStageInviteDto filterStageInviteDto){
        return service.getFilteredInvites(userId, filterStageInviteDto);
    }
}
