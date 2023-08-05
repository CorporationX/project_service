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
public class StageController {

    private final StageService stageService;

    @PostMapping("/stage")//поменяю в будущем когда договоримся об общем виде
    public StageDto createProjectStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @PutMapping("/stage/{stageId}")
    public StageDto updateStageRoles(@PathVariable long stageId,@Valid @RequestBody StageRolesDto stageRolesDto) {
        return stageService.updateStage(stageId, stageRolesDto);
    }

    @DeleteMapping("/stage/{stageId}")
    public void deleteStageWithTasks(@PathVariable long stageId) {
        stageService.deleteStageWithTasks(stageId);
    }

    @DeleteMapping("/stage/close/{stageId}")
    public void deleteStageCloseTasks(@PathVariable long stageId) {
        stageService.deleteStageCloseTasks(stageId);
    }

    @DeleteMapping("/stage/{stageId}/{stageToUpdateId}")
    public void deleteStageTransferTasks(@PathVariable long stageId, @PathVariable long stageToUpdateId) {
        stageService.deleteStageTransferTasks(stageId, stageToUpdateId);
    }

    @GetMapping("/{projectId}/stage")
    public List<StageDto> getAllProjectStages(@PathVariable long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/stage/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }
}
