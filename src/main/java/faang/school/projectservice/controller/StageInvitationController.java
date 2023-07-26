package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.filters.stageInvites.FilterStageInviteDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invites")
public class StageInvitationController {
    private final StageInvitationService service;

    public StageInvitationDto sendInvite(@Valid StageInvitationDto stageInvitationDto){
        return service.sendInvite(stageInvitationDto);
    }

    public StageInvitationDto acceptInvite(@Valid StageInvitationDto stageInvitationDto){
        return service.acceptInvite(stageInvitationDto);
    }

    public StageInvitationDto rejectInvite(@Valid StageInvitationDto stageInvitationDto){
        return service.rejectInvite(stageInvitationDto);
    }

    public List<StageInvitationDto> getFilteredInvites(@Valid Long userId,
                                                       @Valid FilterStageInviteDto filterStageInviteDto){
        return service.getFilteredInvites(userId, filterStageInviteDto);
    }
}
