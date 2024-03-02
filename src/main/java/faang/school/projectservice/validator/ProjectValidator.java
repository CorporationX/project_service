package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProjectValidator {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    public void validateToCreate(ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String name = projectDto.getName();

        validateAccessToProject(ownerId);
        validateNameExistence(ownerId, name);
        validateName(name);
        validateDescription(projectDto.getDescription());
    }

    public void validateStatus(List<Project> children, ProjectStatus updatedStatus) {
        validateStatuses(children, updatedStatus);
        validateSubProjectCompleted(children, updatedStatus);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name of project cannot be empty or blank");
        }
    }

    public void validateDescription(String description) {
        if (description != null && description.isBlank()) {
            throw new ValidationException("Description of project cannot be empty or blank");
        }
    }

    public void validateAccessToProject(long ownerId) {
        if (!haveAccessToProject(ownerId)) {
            throw new SecurityException("User is not the owner of the project");
        }
    }

    public boolean haveAccessToProject(long ownerId) {
        return userContext.getUserId() == ownerId;
    }

    public void validateVisibility(Project project) {
        if (!project.getVisibility().equals(project.getParentProject().getVisibility())) {
            throw new ValidationException("Visibility not valid");
        }
    }

    private void validateNameExistence(long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new ValidationException("Project with this name already exists. Name: " + name);
        }
    }

    private void validateSubProjectCompleted(List<Project> children, ProjectStatus updatedStatus) {
        if (updatedStatus.equals(ProjectStatus.COMPLETED) &&
                !children.stream()
                        .allMatch(childProject -> childProject.getStatus().equals(ProjectStatus.COMPLETED))) {
            throw new ValidationException("SubProjects not completed");
        }
    }

    private void validateStatuses(List<Project> children, ProjectStatus updatedStatus) {
        if (!children.stream()
                .allMatch(childProject -> childProject.getStatus().equals(updatedStatus))) {
            throw new ValidationException("Statuses not equals");
        }
    }
}