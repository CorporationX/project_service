package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateProjectCreate(Project project) {
        validateFields(project);
        validateProjectNameExist(project);
    }

    public void validateProjectUpdate(Project project) {
        validateProjectExists(project);
        validateFields(project);
    }

    private void validateFields(Project project) {
        validateName(project);
        validateDescription(project);
        validateProjectVisibility(project);
    }

    private void validateName(Project project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new DataValidationException("Project must have title");
        }
    }

    private void validateDescription(Project project) {
        if (project.getDescription() == null || project.getDescription().isBlank()) {
            throw new DataValidationException("Project must have description");
        }
    }

    private void validateProjectNameExist(Project project) {
        boolean isProjectExist = projectRepository.existsByOwnerUserIdAndName(project.getOwnerId(), project.getName());
        if (isProjectExist) {
            throw new DataValidationException("User already have project with same name");
        }
    }

    private void validateProjectVisibility(Project project) {
        if (project.getVisibility() == null) {
            throw new DataValidationException("Project must have visibility");
        }
    }

    private void validateProjectExists(Project project) {
        if (!projectRepository.existsById(project.getId())) {
            throw new EntityNotFoundException(String.format("Project with ID %d not found", project.getId()));
        }
    }
}
