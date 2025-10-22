package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.project.DuplicateResourceException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.Objects;


public class ProjectValidator {

    public static void validateUniqueProjectNameForOwner(String projectName, Long ownerId, boolean nameExists) {
        if (nameExists) {
            throw new DuplicateResourceException("Project with this name already exists for this user");
        }
    }

    public static void validateProjectExists(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
    }

    public static void validateAccessToProject(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            boolean isParticipant = project.getTeams() != null && project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(p -> Objects.equals(p.getId(), userId));
            if (!isParticipant) {
                throw new IllegalArgumentException("Access denied to private project");
            }
        }
    }

    public static void validateUpdate(Project project, ProjectStatus status, String description) {
        if (status == null && description == null) {
            throw new IllegalArgumentException("Nothing to update");
        }
        validateProjectExists(project);
    }
}