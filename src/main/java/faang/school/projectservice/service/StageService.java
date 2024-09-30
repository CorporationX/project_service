package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.filter.StageFilterDto;

import java.util.List;

public interface StageService {
    void create(StageDto stageDto);

    List<StageDto> getAllStages(Long projectId);

    StageDto getStageById(Long id);

    void deleteStage(StageDto stageDto);

    List<StageDto> getFilteredStages(Long projectId, StageFilterDto filterDto);

    void updateStage(StageDto stageDto);
}
