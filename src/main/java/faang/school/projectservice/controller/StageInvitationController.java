package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.AcceptInviteDto;
import faang.school.projectservice.dto.stage_invitation.RejectInviteDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/stage-invitation/")
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService service;

    @PostMapping
    public StageInvitationDto sendInvite(@RequestBody StageInvitationDto request) {
        return service.sendInvite(request);
    }

    @PostMapping("/accept")
    public StageInvitationDto acceptInvite(@RequestBody AcceptInviteDto request) {
        return service.acceptInvite(request);
    }

    @PostMapping("/reject")
    public StageInvitationDto rejectInvite(@Valid @RequestBody RejectInviteDto request) {
        return service.rejectInvite(request);
    }

    @PostMapping("/find")
    public List<StageInvitationDto> findByFilter(@RequestBody StageInvitationFilterDto filter) {
        return service.findByFilter(filter);
    }
}
