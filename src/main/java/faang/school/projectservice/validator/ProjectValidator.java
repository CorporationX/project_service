package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectValidator {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    public void validateToCreate (ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String name = projectDto.getName();

        validateAccessToProject(ownerId);
        validateNameExistence(ownerId, name);
        validateName(name);
        validateDescription(projectDto.getDescription());
    }

    public void validateName(String name) {
        if (name.isEmpty() || name.isBlank()) {
            throw new ValidationException("Name of project cannot be empty or blank");
        }
    }

    public void validateDescription(String description) {
        if (description != null && (description.isEmpty() || description.isBlank())) {
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

    public void validateNameExistence(long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new IllegalArgumentException("Project with this name already exists. Name: " + name);
        }
    }
}