package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.UpdateStageDto;

import java.util.List;

public interface StageService {
    StageDto createStage(StageDto stageDto);

    List<StageDto> getAllStagesOfProject(Long projectId);

    void deleteById(Long stageId);

    StageDto updateStage(Long stageId, UpdateStageDto updateStageDto);

    StageDto getById(Long stageId);
}
