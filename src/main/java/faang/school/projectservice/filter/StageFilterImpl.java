package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageFilterImpl implements StageFilter {

    @Override
    public boolean isApplicable(StageCreateDto stageCreateDto) {
        return stageCreateDto.project() != null;
    }

    @Override
    public List<Stage> applyByRoleAndStatus(Project project, TeamRole TeamRolefilter, TaskStatus taskStatusFilter) {
        return project.getStages().stream()
                .filter(stage -> stage.getStageRoles().stream()
                        .map(StageRoles::getTeamRole)
                        .anyMatch(teamRole -> teamRole.equals(TeamRolefilter)))
                .filter(stage -> stage.getTasks().stream()
                        .map(Task::getStatus)
                        .anyMatch(taskStatus -> taskStatus.equals(taskStatusFilter))
                )
                .toList();
    }
}