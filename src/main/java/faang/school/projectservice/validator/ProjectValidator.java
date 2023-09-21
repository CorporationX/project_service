package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;
    public void existProjectValidator(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project not found by id: %s", projectId));
        }
    }
}
