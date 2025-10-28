package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.AllStageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StageFilter {

private final StageRepository stageRepository;

    public List<Stage> applyByRoleAndStatus(AllStageFilterDto allStageFilterDto) {
        long projectId = allStageFilterDto.projectId();
        List<TeamRole> teamRoles = allStageFilterDto.teamRoleList();
        TaskStatus taskStatus = allStageFilterDto.taskStatus();
        return stageRepository.getAllStageByTaskStatusAndTeamRole(projectId, taskStatus, teamRoles);
    }
}