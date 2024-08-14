package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void existsProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new DataValidationException(String.format("Project [%s] id doesn't exist", id));
        }
    }
}
