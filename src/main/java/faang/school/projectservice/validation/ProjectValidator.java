package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateProjectCreate(ProjectDto project) {
        validateFields(project);
        validateProjectNameExist(project);
    }

    public void validateProjectUpdate(ProjectDto project) {
        validateProjectExists(project);
        validateFields(project);
    }

    private void validateFields(ProjectDto project) {
        validateName(project);
        validateDescription(project);
        validateProjectVisibility(project);
    }

    private void validateName(ProjectDto project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new DataValidationException("Project must have title");
        }
    }

    private void validateDescription(ProjectDto project) {
        if (project.getDescription() == null || project.getDescription().isBlank()) {
            throw new DataValidationException("Project must have description");
        }
    }

    private void validateProjectNameExist(ProjectDto project) {
        boolean isProjectExist = projectRepository.existsByOwnerUserIdAndName(project.getOwnerId(), project.getName());
        if (isProjectExist) {
            throw new DataValidationException("User already have project with same name");
        }
    }

    private void validateProjectVisibility(ProjectDto project) {
        if (project.getVisibility() == null) {
            throw new DataValidationException("Project must have visibility");
        }
    }

    private void validateProjectExists(ProjectDto project) {
        if (!projectRepository.existsById(project.getId())) {
            throw new EntityNotFoundException(String.format("Project with ID %d not found", project.getId()));
        }
    }
}
