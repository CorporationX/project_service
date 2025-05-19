package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;

import java.util.List;

public interface StageService {
    public StageDto findById(Long id);
    public List<StageDto> findAllStages(Long projectId);
    public void updateStage(StageDto stageDto);
    public void deleteStage(Long id, StageDto stageDto);
    public List <StageDto> getStagesWithFilters(TeamRole teamRole, TaskStatus taskStatus);
    public void save (StageDto stageDto);
}
