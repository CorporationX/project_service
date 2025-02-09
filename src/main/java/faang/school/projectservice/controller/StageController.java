package faang.school.projectservice.controller;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/{projectId}/stages")
public class StageController {

    private final StageService stageService;

    @PostMapping("/stage")
    public StageDto createStage(@Valid @PathVariable Long projectId,
                                @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/stages")
    public ResponseEntity<List<StageDto>> getStages(@Valid @PathVariable Long projectId,
                                                    @RequestParam(required = false) Set<StageRoles> roles,
                                                    @RequestParam(required = false) TaskStatus taskStatus) {
        return ResponseEntity.ok(stageService.getStages(projectId));
    }

    @DeleteMapping("/{stageId}")
    public void deleteStage(@Valid @PathVariable Long stageId,
                            @PathVariable Long projectId) {
        stageService.deleteStage(stageId);
        log.info("Удален этап {} проекта {}", stageId, projectId);
    }

    @PutMapping("/{stageId}")
    public StageUpdateDto updateStage(@Valid @PathVariable Long projectId,
                                @PathVariable Long stageId,
                                @RequestBody StageUpdateDto StageUpdateDto) {
        return stageService.updateStage(stageId, StageUpdateDto);
    }


    @PostMapping("/{stageId}/invitations")
    public ResponseEntity<StageInvitationDto> sendStageInvitations(@Valid @PathVariable Long projectId,
                                                                   @PathVariable Long stageId,
                                                                   @RequestBody StageInvitationDto stageInvitationDto) {
        return ResponseEntity.ok((stageService.sendInvitations(stageId,stageInvitationDto)));
    }

    @GetMapping("/{stageId}/tasks")
    public ResponseEntity<List<Task>> getStageTasks(@Valid @PathVariable Long projectId,
                                                    @PathVariable Long stageId,
                                                    @RequestParam(required = false) TaskStatus status) {
        return stageService.getStageTasks(stageId, status);
    }

    @PutMapping("/{stageId}/participants")
    public ResponseEntity<StageUpdateDto> updateStageTeamMember(@Valid @PathVariable Long projectId,
                                                                @PathVariable Long stageId,
                                                                @RequestBody Set<StageUpdateDto> StageUpdateDto) {
        return stageService.updateStageParticipants(StageUpdateDto);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<StageDto>> getFilteredStages(@Valid @PathVariable Long projectId,
                                                            @RequestParam(required = false) Set<TeamRole> roles,
                                                            @RequestParam(required = false) TaskStatus taskStatus) {
        StageFilterDto stageFilter = StageFilterDto.builder()
                .role(roles)
                .taskStatus(taskStatus)
                .build();
        List<StageDto> filteredStages = stageService.getActiveStages(projectId, stageFilter);

        return ResponseEntity.ok(filteredStages);
    }
}
