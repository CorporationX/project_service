package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public interface StageService {
     StageDto findById(Long id);

     List<StageDto> findAllStages(Long projectId);

     void updateStage(StageDto stageDto);

     void deleteStage(Long id, StageDto stageDto);

     List<StageDto> getStagesWithFilters(TeamRole teamRole, TaskStatus taskStatus);

     void save(StageDto stageDto);
}
