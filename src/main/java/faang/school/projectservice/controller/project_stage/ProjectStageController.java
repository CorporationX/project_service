package faang.school.projectservice.controller.project_stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.projectStage.ProjectStageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("project/stages")
public class ProjectStageController {
    private final ProjectStageService projectStageService;

    @PostMapping
    public StageDto createStage(@RequestBody @Valid StageDto stageDto) {
        return projectStageService.createStage(stageDto);
    }

    @GetMapping("/{projectId}")
    public List<StageDto> getStageByStatus(@PathVariable("projectId") Long projectId, @RequestParam TaskStatus status) {
        return projectStageService.getStagesProjectByStatus(projectId, status);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable("stageId") Long stageId) {
        projectStageService.deleteStage(stageId);
    }

    @PutMapping("/{stageId}")
    public StageDto updateStage(@PathVariable("stageId") Long stageId) {
        return projectStageService.updateStage(stageId);
    }

    @GetMapping("/project/{projectId}")
    public List<StageDto> getAllStages(@PathVariable("projectId") Long projectId) {
        return projectStageService.getAllStages(projectId);
    }

    @GetMapping("/get/{stageId}")
    public StageDto getStageById(@PathVariable("stageId") Long stageId) {
        return projectStageService.getStage(stageId);
    }
}
