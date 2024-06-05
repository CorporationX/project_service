package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.model.StageDeleteMode;
import faang.school.projectservice.model.StageStatus;

import java.util.List;

public interface StageService {
    StageDto createStage(NewStageDto dto);

    List<StageDto> getAllStages(Long projectId, StageStatus statusFilter);

    List<StageDto> getAllStages(Long projectId);

    void deleteStage(long stageId, Long stageToMigrateId, StageDeleteMode stageDeleteMode);

    StageDto updateStage(Long stageId, List<NewStageRolesDto> newStageRolesDtoList);

    StageDto getStageById(Long stageId);
}
