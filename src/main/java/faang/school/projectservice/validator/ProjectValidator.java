package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void isExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Entity project with projectId=" + projectId + " not found.");
        }
    }

    public void validateProjectParams(Project project) {
        boolean isUniq = projectRepository.existsByOwnerUserIdAndName(project.getOwnerId(), project.getName());
        if (Objects.isNull(project.getName()) || project.getName().isEmpty()) {
            throw new DataValidationException("Project name must not be null or empty.");
        } else if (Objects.isNull(project.getDescription()) || project.getDescription().isEmpty()) {
            throw new DataValidationException("Project description must not be null or empty.");
        } else if (isUniq) {
            throw new DataValidationException("A project with the same owner and name exists.");
        }
    }
}
