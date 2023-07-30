package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    @PostMapping("/project")
    public StageDto createProjectStage(@RequestBody StageDto stageDto) {
        validateStageName(stageDto);

        return stageService.create(stageDto);
    }

    @DeleteMapping("/stage/{stageId}")
    public void deleteStageWithTasks(@PathVariable long stageId) {
        stageService.deleteStageWithTasks(stageId);
    }

    @DeleteMapping("/stage/close/{stageId}")
    public void deleteStageCloseTasks(@PathVariable long stageId) {
        stageService.deleteStageCloseTasks(stageId);
    }

    @DeleteMapping("/stage/{stageId}/{stageToTransferId}")
    public void deleteStageTransferTasks(@PathVariable long stageId, @PathVariable long stageToTransferId) {
        stageService.deleteStageTransferTasks(stageId, stageToTransferId);
    }

    @GetMapping("/project/{projectId}")
    public List<StageDto> getAllProjectStages(@PathVariable long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/stage")
    public StageDto getStageById(long stageId) {
        return stageService.getStageById(stageId);
    }

    private void validateStageName(StageDto stageDto) {
        if (stageDto.getStageName() == null || stageDto.getStageName().isBlank()) {
            throw new DataValidationException("Stage name can't be blank or null");
        }
    }
}
