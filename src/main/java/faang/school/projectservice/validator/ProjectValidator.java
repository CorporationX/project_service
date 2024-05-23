package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void isExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Entity project with projectId=" + projectId + " not found.");
        }
    }
}
