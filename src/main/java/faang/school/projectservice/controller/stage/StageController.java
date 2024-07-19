package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.ProjectStatus;
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

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping("/create")
    public StageDto create(@RequestBody @Valid StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @GetMapping("/get")
    public List<StageDto> findByStatus(@RequestBody ProjectStatus status) {
        return stageService.getByStatus(status);
    }

    @DeleteMapping("/delete/{stageId}")
    public StageDto deleteStageById(@PathVariable Long stageId) {
        return stageService.deleteStageById(stageId);
    }

    @DeleteMapping("/delete/{stageId}/close-tasks")
    public StageDto deleteStageWithClosingTasks(@PathVariable Long stageId) {
        return stageService.deleteStageWithClosingTasks(stageId);
    }

    @PutMapping("/update/{stageId}")
    public StageDto update(@PathVariable Long stageId, @RequestBody @Valid StageDto stageDto) {
        return stageService.update(stageId, stageDto);
    }

    @GetMapping
    public List<StageDto> getAll() {
        return stageService.getAll();
    }

    @GetMapping("/{stageId}")
    public StageDto getById(@PathVariable long stageId) {
        return stageService.getById(stageId);
    }
}
