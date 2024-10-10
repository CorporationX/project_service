package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.StageDto;
import faang.school.projectservice.model.dto.StageFilterDto;
import faang.school.projectservice.model.enums.TasksAfterDelete;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @PostMapping("/filter")
    public List<StageDto> getFilteredStages(@RequestBody StageFilterDto filterDto) {
        return stageService.getFilteredStages(filterDto);
    }

    @DeleteMapping("/{deletedStageId}")
    public void deleteStage(@Positive @PathVariable Long deletedStageId,
                            @RequestParam TasksAfterDelete tasksAfterDelete,
                            @RequestParam Long receivingStageId) {

        stageService.deleteStage(deletedStageId, tasksAfterDelete, receivingStageId);
    }

    @PutMapping
    public StageDto updateStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.updateStage(stageDto);
    }

    @GetMapping
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @GetMapping("/{id}")
    public StageDto getStage(@Positive @PathVariable Long id) {
        return stageService.getStage(id);
    }
}
