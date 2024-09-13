package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;

import java.util.List;

public interface StageService {
    StageDto createStage(StageDto stageDto);

    List<StageDto> getProjectStages(long id, StageFilterDto filters);

    void deleteStage(long stageId);

    StageDto updateStage(long stageId, StageRolesDto stageRolesDto);

    List<StageDto> getStages(long projectId);

    StageDto getSpecificStage(long stageId);
}
