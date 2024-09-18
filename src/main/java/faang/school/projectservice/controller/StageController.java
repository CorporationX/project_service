package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategy;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
public class StageController {
    private final StageService stageService;

    @Transactional
    @PostMapping("/stage")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/stage/filters")
    public List<StageDto> getFilteredStagesByRolesAndStatus(
            @RequestParam Long projectId,
            @RequestParam List<TeamRole> roles,
            @RequestParam List<TaskStatus> taskStatuses) {

        return stageService.getFilteredStagesByRolesAndStatus(projectId, roles, taskStatuses);
    }

    @Transactional
    @DeleteMapping("/stage/{providerStageId}")
    public void deleteStage( @PathVariable @Positive Long providerStageId,
                            @RequestParam DeleteStageStrategy taskAction,
                            @RequestParam @Positive Long consumerStageId) {

        stageService.deleteStage(providerStageId, taskAction, consumerStageId);
    }

    @GetMapping("/project/{projectId}/stages")
    public List<StageDto> getAllStagesByProjectId(@PathVariable @Positive  Long projectId) {
        return stageService.getAllStagesByProjectId(projectId);
    }

    @GetMapping("stage/{stageId}")
    public StageDto getStageById(@PathVariable @Positive Long stageId) {

        return stageService.getStageById(stageId);
    }

    @Transactional
    @PutMapping("/stage")
    public StageDto updateStage(@RequestBody @Valid StageDto stageDto) {

        return stageService.updateStage(stageDto);
    }


}
