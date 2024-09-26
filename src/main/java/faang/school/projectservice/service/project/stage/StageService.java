package faang.school.projectservice.service.project.stage;

import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;

import java.util.List;

public interface StageService {
    StageDto createStage(StageCreateDto stageCreateDto);

    List<StageDto> getStages(Long projectId, StageFilterDto filters);

    StageDto removeStage(Long stageId, RemoveTypeDto removeTypeDto);

    StageDto updateStage(StageUpdateDto stageUpdateDto, Long userId, Long stageId);

    StageDto getStage(Long stageId);
}
