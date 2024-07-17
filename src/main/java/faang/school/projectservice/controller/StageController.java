package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.FateOfTasksAfterDelete;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    public StageDto createStage(@Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    public List<StageDto> getFilteredStages(StageFilterDto filterDto) {
        return stageService.getFilteredStages(filterDto);
    }

    public void deleteStage(Long deletedStageId, FateOfTasksAfterDelete tasksAfterDelete, Long receivingStageId) {
        stageService.deleteStage(deletedStageId, tasksAfterDelete, receivingStageId);
    }

    public StageDto updateStage(@Valid StageDto stageDto) {
        return stageService.updateStage(stageDto);
    }

    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    public StageDto getStage(Long id) {
        return stageService.getStage(id);
    }
}
