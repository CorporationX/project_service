package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/stages")
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

    @DeleteMapping("/delete/{stageId}")
    public void deleteStageWithTasks(@PathVariable long stageId) {
        stageService.deleteStageWithTasks(stageId);
    }

    @DeleteMapping("/close/{stageId}")
    public void deleteStageCloseTasks(@PathVariable long stageId) {
        stageService.deleteStageCloseTasks(stageId);
    }

    @DeleteMapping("/delete/{stageId}/transfer/{stageToUpdateId}")
    public void deleteStageTransferTasks(@PathVariable long stageId, @PathVariable long stageToUpdateId) {
        stageService.deleteStageTransferTasks(stageId, stageToUpdateId);
    }

    @GetMapping("/search/project/{projectId}")
    public List<StageDto> getAllProjectStages(@PathVariable long projectId) {
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/search/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }
}
