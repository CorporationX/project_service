package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.project.AccessDeniedException;
import faang.school.projectservice.exception.project.BadRequestException;
import faang.school.projectservice.exception.project.DuplicateResourceException;
import faang.school.projectservice.exception.project.ResourceNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Supplier;


@UtilityClass
public class ProjectValidator {

    public void validateUniqueProjectNameForOwner(
            String projectName,
            Long ownerId,
            Supplier<Boolean> nameExistsSupplier
    ) {
        if (nameExistsSupplier.get()) {
            throw new DuplicateResourceException(
                    String.format("Project with name '%s' already exists for user %d", projectName, ownerId)
            );
        }
    }

    public void validateProjectExists(Project project) {
        if (project == null) {
            throw new ResourceNotFoundException("Project not found");
        }
    }

    public void validateAccessToProject(Project project, Long userId) {
        validateProjectExists(project);

        if (project.getVisibility() == ProjectVisibility.PRIVATE && !isUserParticipant(project, userId)) {
            throw new AccessDeniedException("Access denied to private project");
        }
    }

    public void validateUpdate(Project project, ProjectStatus status, String description) {
        validateProjectExists(project);
        if (status == null && description == null) {
            throw new BadRequestException("Nothing to update");
        }
    }

    public boolean isUserParticipant(Project project, Long userId) {
        return project.getTeams() != null &&
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .anyMatch(member -> Objects.equals(member.getId(), userId));
    }
}