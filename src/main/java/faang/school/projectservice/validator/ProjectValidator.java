package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateCreateProject(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getDescription() == null) {
            throw new DataValidationException("Project name and description cannot be null");
        }
        List<Project> projects = projectRepository.findAll();
        Optional<Project> projectExists = projects.stream()
                .filter(project -> project.getOwnerId().equals(projectDto.getOwnerId())).findFirst();
        if (projectExists.isPresent() && projectExists.get().getName().equals(projectDto.getName())) {
            throw new DataValidationException("Project already exists");
        }
    }

    public void validateUpdateProject(Long id, ProjectUpDateDto projectUpDateDto) {
        if (id == null || id <= 0 || projectUpDateDto.getDescription() == null && projectUpDateDto.getStatus() == null) {
            throw new DataValidationException("Check for id or description or status");
        }
    }

    public void validateFilter(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto.getStatus() == null && projectFilterDto.getName() == null) {
            throw new DataValidationException("Check for filter");
        }
    }

    public void validateProjectId(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Check for id");
        }
    }

    public Project validateServiceGetProject(Long userId, Project project) {
        Project projectValidate = null;
        List<Team> teams = project.getTeams();
        if (project.getVisibility() == PRIVATE) {
            for (Team team : teams) {
                for (var teamMember : team.getTeamMembers()) {
                    if (teamMember.getId().equals(userId)) {
                        projectValidate = project;
                        break;
                    }
                } // Пишу для очередного желающего сказать, что этот метод никогда не возвращает null.
            } // Возвращает и еще как ! Да так возвращает, что ломал мне метод фильтрации.
        } else {
            projectValidate = project;
        }
        return projectValidate;
    }

    public void validateServiceOwnerOfProject(Long userId, Project project) {
        if (!userId.equals(project.getOwnerId())) {
            throw new DataValidationException("You are not owner of project");
        }
    }
}
