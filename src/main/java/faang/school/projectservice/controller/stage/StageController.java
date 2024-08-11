package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.validator.stage.StageControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController()
@RequiredArgsConstructor
@RequestMapping("/stage")
@Tag(name = "Stages", description = "Stages handler")
public class StageController {
    private StageService service;
    private StageControllerValidator validator;

    @Operation(summary = "Create new stage")
    @PostMapping("/create")
    public StageDto create(@RequestBody StageDto stage) {
        validator.validate(stage);
        return service.createStage(stage);
    }

    @Operation(summary = "Get all stages")
    @GetMapping("/getAllByStatus")
    public List<StageDto> getAllByStatus(@RequestParam @NonNull ProjectStatus status) {
        return service.getAllByStatus(status);
    }

    @Operation(summary = "Handle stage")
    @DeleteMapping("/handle/{stageId}")
    public void handle(@PathVariable long stageId, @RequestParam @NonNull StageWithTasksAction action, @RequestParam(required = false) Long stageToMoveId) {
        service.handle(stageId, action, stageToMoveId);
    }

    @Operation(summary = "Update stage")
    @PutMapping("/update/{stageId}")
    public StageDto update(@RequestHeader("x-user-id") long userId, @PathVariable long stageId, @RequestBody StageDto stageDto) {
        return service.update(userId, stageId, stageDto);
    }

    @Operation(summary = "Get all stages of project")
    @GetMapping("/getAll/{projectId}")
    public List<StageDto> getAll(@PathVariable long projectId) {
        return service.getAll(projectId);
    }

    @Operation(summary = "Get all stages of project")
    @GetMapping("/getStage/{stageId}")
    public StageDto getById(@PathVariable long stageId) {
        return service.getById(stageId);
    }
}
