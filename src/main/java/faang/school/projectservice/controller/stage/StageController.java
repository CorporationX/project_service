package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stages")
public class StageController {

    private final StageService stageService;

    @PostMapping
    public StageDto createProjectStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @PutMapping("/{stageId}")
    public StageDto updateStageRoles(@PathVariable long stageId, @Valid @RequestBody StageRolesDto stageRolesDto) {
        return stageService.updateStage(stageId, stageRolesDto);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStageWithTasks(@PathVariable long stageId) {
        stageService.deleteStageWithTasks(stageId);
    }

    @DeleteMapping("/delete/{stageId}")
    public void deleteStageCloseTasks(@PathVariable long stageId) {
        stageService.deleteStageCloseTasks(stageId);
    }

    @DeleteMapping("/{stageId}/{stageToUpdateId}")
    public void deleteStageTransferTasks(@PathVariable long stageId, @PathVariable long stageToUpdateId) {
        stageService.deleteStageTransferTasks(stageId, stageToUpdateId);
    }

    @GetMapping("/project/{projectId}")
    public List<StageDto> getAllProjectStages(@PathVariable long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }
}
