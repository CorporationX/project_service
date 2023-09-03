package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;
    @PostMapping
    public StageInvitationDto create(@RequestBody @Valid StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    @PutMapping("/accept/{invitationId}")
    public StageInvitationDto accept(@PathVariable long invitationId) {
        return stageInvitationService.accept(invitationId);
    }

    @PutMapping("/reject/{invitationId}")
    public StageInvitationDto reject(@PathVariable long invitationId, @RequestParam String message) {
        return stageInvitationService.reject(invitationId, message);
    }

    @GetMapping("/list/{userId}")
    public List<StageInvitationDto> getStageInvitationFilter(
            @RequestBody @Valid StageInvitationFilterDto stageInvitationFilterDto,
            @PathVariable long userId) {
        return stageInvitationService.getStageInvitationFilter(stageInvitationFilterDto, userId);
    }
}
