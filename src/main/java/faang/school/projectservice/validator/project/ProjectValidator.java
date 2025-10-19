package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public ProjectValidator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void validateUniqueProjectNameForOwner(String projectName, Long ownerId) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectName)) {
            throw new IllegalArgumentException("Project with this name already exists for user");
        }
    }

    public void validateProjectExists(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
    }

    public void validateAccessToProject(Project project, Long userId) {
        if (project.getVisibility() != null && project.getVisibility() == ProjectVisibility.PRIVATE) {
            boolean isParticipant = project.getTeams() != null && project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(p -> Objects.equals(p.getId(), userId));
            if (!isParticipant) {
                throw new IllegalArgumentException("Access denied to private project");
            }
        }
    }

    public void validateUpdate(Project project, ProjectStatus status, String description) {
        if (status == null && description == null) {
            throw new IllegalArgumentException("Nothing to update");
        }
        validateProjectExists(project);
    }
}
