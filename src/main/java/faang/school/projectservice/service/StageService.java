package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;

import java.util.List;

public interface StageService {
    StageDto createStage(StageDto stageDto);

    StageDto updateStage(StageDto stageDto);

    List<StageDto> getAllStagesByFilter(StageFilterDto filter);

    List<StageDto> getAllStages();

    StageDto getStageById(long stageId);

    void deleteStage(long stageId);
}
