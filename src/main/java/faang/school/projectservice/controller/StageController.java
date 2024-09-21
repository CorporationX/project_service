package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.strategy.DeleteStageTaskStrategy;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
public class StageController {
    private final StageService stageService;

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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStage( @PathVariable @Positive Long providerStageId,
                            @RequestParam DeleteStageTaskStrategy taskStrategy,
                            @RequestParam @Positive Long consumerStageId) {

        stageService.deleteStage(providerStageId, taskStrategy, consumerStageId);
    }

    @Transactional(readOnly = true)
    @GetMapping("/project/{projectId}/stages")
    public List<StageDto> getAllStagesByProjectId(@PathVariable @Positive  Long projectId) {
        return stageService.getAllStagesByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    @GetMapping("stage/{stageId}")
    public StageDto getStageById(@PathVariable @Positive Long stageId) {

        return stageService.getStageDtoById(stageId);
    }

    @Transactional
    @PutMapping("/stage")
    public StageDto updateStage(@RequestBody @Valid StageDto stageDto) {

        return stageService.updateStage(stageDto);
    }


}
