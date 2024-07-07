package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.service.stage.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stage-invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationDto create(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    @GetMapping("/{id}")
    public List<StageInvitationDto> getStageInvitations(@PathVariable("id") long id, @Valid @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getUserStageInvitations(id, filter);
    }

    @PutMapping("/accept")
    public StageInvitationDto accept(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.accept(stageInvitationDto);
    }

    @PutMapping("/reject")
    public StageInvitationDto reject(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.reject(stageInvitationDto);
    }
}
