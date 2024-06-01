package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("stages")
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@RequestBody @Valid StageDto stageDto) {
        log.info("Create stage");
        return stageService.createStage(stageDto);
    }

    @GetMapping("/{projectId}")
    public List<StageDto> getFilterStageByStatus(@PathVariable("projectId") long projectId, @RequestParam("status") TaskStatus status) {
        return stageService.getStagesProjectByStatus(projectId, status);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable("stageId") Long stageId) {
        stageService.deleteStage(stageId);
    }

    @PutMapping("/{stageId}")
    public StageDto updateStage(@PathVariable("stageId") Long stageId) {
        return stageService.updateStage(stageId);
    }

    @GetMapping("/project/{projectId}")
    public List<StageDto> getAllStages(@PathVariable("projectId") Long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping("/sadf/{stageId}")
    public StageDto getStageById(@PathVariable("stageId") Long stageId) {
        return stageService.getStage(stageId);
    }
}
