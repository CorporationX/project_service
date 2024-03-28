package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validatorOpenProject(List<Long> projectIds) {
        List<Project> projects = projectRepository.findAllByIds(projectIds);
        projects.stream()
                .filter(project -> project.getStatus().equals(ProjectStatus.CANCELLED))
                .forEach(project -> {throw new IllegalArgumentException("ProjectStatus.CANCELLED");});
    }

    public void validateProjectCreate(ProjectDto project) {
        validateProjectVisibility(project);
        validateProjectNameExist(project);
    }

    public void validateProjectUpdate(ProjectDto project) {
        validateProjectExists(project);
        validateProjectVisibility(project);
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
