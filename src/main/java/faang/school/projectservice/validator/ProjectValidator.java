package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
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

    public void isUniqOwnerAndName(long userId, String name) {
        if (!projectRepository.existsByOwnerUserIdAndName(userId, name)) {
            throw new DataValidationException("A project with the same owner and name exists.");
        }
    }

    public void nameExistsAndNotAmpty(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new DataValidationException("Project name must not be null or empty.");
        }
    }

    public void descExistsAndNotEmpty(String description) {
        if(Objects.isNull(description) || description.isEmpty()) {
            throw new EntityNotFoundException("Project description must not be null or empty.");
        }
    }
}
