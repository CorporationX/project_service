package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;

import java.util.List;

public interface StageService {
    StageDto createStage(StageDto stageDto);
    List<StageDto> getProjectStages(long id, StageFilterDto filters);
    void deleteStage(long stageId);
    StageDto updateStage(long stageId);
}
