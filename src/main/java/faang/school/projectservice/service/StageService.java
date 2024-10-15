package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.StageDto;
import faang.school.projectservice.model.dto.StageFilterDto;
import faang.school.projectservice.model.enums.TasksAfterDelete;

import java.util.List;

public interface StageService {
    StageDto createStage(StageDto stageDto);

    List<StageDto> getFilteredStages(StageFilterDto filterDto);

    void deleteStage(Long deletedStageId, TasksAfterDelete tasksAfterDelete, Long receivingStageId);

    StageDto updateStage(StageDto stageDto);

    List<StageDto> getAllStages();

    StageDto getStage(Long id);
}
