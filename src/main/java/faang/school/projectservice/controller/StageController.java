package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.*;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping("/create")
    public StageResponse createStage(@RequestBody @Valid CreateStageRequest createStageRequest) {
        return stageService.create(createStageRequest);
    }

    @DeleteMapping("/delete")
    public void deleteStage(@RequestBody @Valid DeleteStageRequest deleteStageRequest) {
        stageService.deleteStageWithStrategy(deleteStageRequest);
    }

    @PutMapping("/update")
    public StageResponse updateStage(@RequestBody @Valid UpdateStageRequest updateStageRequest) {
        return stageService.update(updateStageRequest);
    }

    @GetMapping("/{projectId}/filter")
    public List<StageResponse> getStagesByProject(@PathVariable @NotNull Long projectId,
                                                  @RequestParam(required = false) List<String> roles,
                                                  @RequestParam(required = false) String taskStatus) {
        return stageService.getStagesByProjectWithFilters(projectId, roles, taskStatus);
    }

    @GetMapping("/{projectId}/all")
    public List<StageResponse> getAllStagesByProject(@PathVariable Long projectId) {
        return stageService.getAllStagesByProject(projectId);
    }

    @GetMapping("/{stageId}")
    public StageResponse getStageById(@PathVariable Long stageId) {
        return stageService.getStageById(stageId);
    }
}