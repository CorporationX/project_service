package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateProject(ProjectDto projectDto) {
        validateField(projectDto.getName(), "Field name cannot be empty or null");
        validateField(projectDto.getDescription(), "Field description cannot be empty or null");
        validateOwnerHasUniqueProjectName(projectDto);
    }

    public void validateUpdatedFields(ProjectDto projectDto) {
        boolean isDescriptionValid = isFieldValid(projectDto.getDescription());
        boolean isStatusValid = projectDto.getStatus() != null;

        if (!isDescriptionValid && !isStatusValid) {
            throw new DataValidationException("At least one updated field must not be empty");
        }
    }

    public void validateProjects(List<Project> projects) {
        validateProjectsExist(projects, "The list of projects doesn't exist");
        validateNoCancelledProjects(projects);
    }

    private void validateProjectsExist(List<Project> projects, String errorMessage) {
        if (projects.isEmpty()) {
            throw new DataValidationException(errorMessage);
        }
    }

    private void validateNoCancelledProjects(List<Project> projects) {
        projects.stream()
                .filter(this::isCancelled)
                .findFirst()
                .ifPresent(project -> {
                    throw new DataValidationException("Cancelled project with ID " + project.getId());
                });
    }

    private boolean isCancelled(Project project) {
        return project.getStatus().equals(ProjectStatus.CANCELLED);
    }

    private void validateOwnerHasUniqueProjectName(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Owner already has a project with name " + projectDto.getName());
        }
    }

    private void validateField(String field, String errorMessage) {
        if (field == null || field.isBlank()) {
            throw new DataValidationException(errorMessage);
        }
    }

    private boolean isFieldValid(String field) {
        return field != null && !field.isBlank();
    }
}
