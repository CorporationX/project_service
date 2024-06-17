package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void exists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Entity project with projectId=" + projectId + " not found.");
        }
    }

    public void validateProjectIsUniqByIdAndName(Project project) {
        boolean unique = projectRepository.existsByOwnerUserIdAndName(project.getOwnerId(), project.getName());
        if (unique) {
            throw new DataValidationException("A project with the same owner and name exists.");
        }
    }
}
