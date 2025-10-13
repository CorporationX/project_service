package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.StageRequestAllStageDto;
import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class StageFilterImpl implements StageFilter {

private final ProjectRepository projectRepository;

    @Override
    public List<Stage> applyByRoleAndStatus(StageRequestAllStageDto stageRequestAllStageDto) {
        Project project = projectRepository.findById(stageRequestAllStageDto.projectId()).get();
        Set<TeamRole> teamRoleFilter = new HashSet<>(stageRequestAllStageDto.teamRoleList());
        return project.getStages().stream()
                .filter(stage -> stage.getStageRoles().stream()
                        .map(StageRoles::getTeamRole)
                        .anyMatch(teamRoleFilter::contains))
                .filter(stage -> stage.getTasks().stream()
                        .map(Task::getStatus)
                        .anyMatch(taskStatus -> taskStatus.equals(stageRequestAllStageDto.taskStatus()))
                )
                .toList();
    }
}