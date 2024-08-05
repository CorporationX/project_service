package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectUtilService {

    private final ProjectRepository projectRepository;

    public List<Project> getAllByIdsStrictly(Collection<Long> ids) {
        List<Project> projects = projectRepository.findAllByIds(ids);
        if (ids.size() != projects.size()) {
            throw new NotFoundException(ErrorMessage.SOME_OF_PROJECTS_NOT_EXIST);
        }
        return projects;
    }

    public List<Project> findAllDistinctByTeamMemberIds(Collection<Long> teamMemberIds) {
        return projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds);
    }

    public void checkProjectsNotClosed(Collection<Project> projects) {
        if (verifyProjectsNotIncludeStatuses(projects, ProjectStatus.COMPLETED, ProjectStatus.CANCELLED)) {
            throw new ConflictException(ErrorMessage.PROJECT_STATUS_INVALID);
        }
    }

    public void checkProjectsFitTeamMembers(Collection<Long> projectIds, Collection<Long> teamMemberIds) {
        // Проверяем относятся ли проекты к переданным пользователям, если существует проект, к
        // которому не относится ни один из пользователей - бросаем исключение
        // Лишний проект / недостающие мемберы

        Set<Long> projectIdsFromMembers =
                findAllDistinctByTeamMemberIds(teamMemberIds).stream()
                        .map(Project::getId)
                        .collect(Collectors.toSet());
        boolean isValid = projectIdsFromMembers.containsAll(projectIds);
        if (!isValid) {
            throw new ConflictException(ErrorMessage.PROJECTS_UNFIT_MEMBERS);
        }
    }

    private boolean verifyProjectsNotIncludeStatuses(Collection<Project> projects, ProjectStatus... invalidStatuses) {
        Set<ProjectStatus> invalidStatusesSet = Set.of(invalidStatuses);
        return projects.stream().noneMatch(project -> invalidStatusesSet.contains(project.getStatus()));
    }
}
