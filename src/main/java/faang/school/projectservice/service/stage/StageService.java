package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;

import java.util.List;

public interface StageService {
    StageDto createStage(StageDto stageDto);

    List<StageDto> getAllStagesByProjectIdAndStatus(Long projectId, String statusFilter);

    StageDto deleteStageById(Long stageId);

    StageDto updateStage(Long stageId, StageDto stageDto);

    List<StageDto> getAllStagesByProjectId(Long projectId);

    StageDto getStageById(Long stageId);
}
