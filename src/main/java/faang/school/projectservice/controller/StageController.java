package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.filter.StageFilterDto;

import java.util.List;

public interface StageController {
    void create(StageDto stageDto);

    List<StageDto> getAllStages(Long projectId);

    StageDto getStageById(Long stageId);

    void deleteStage(StageDto stageDto) ;

    List<StageDto> getFilteredStages(Long projectId, StageFilterDto filterDto);

    void update(StageDto dto);
}
