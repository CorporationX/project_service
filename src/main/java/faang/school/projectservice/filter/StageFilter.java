package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.StageRequestAllStageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Component
public class StageFilter {

private final ProjectRepository projectRepository;



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

    public Specification<List<TeamRole>> getSpecificationTeamRole(List<TeamRole> teamRoles) {

                for(TeamRole role: teamRoles) {
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal("role", role)
                }
    }
}