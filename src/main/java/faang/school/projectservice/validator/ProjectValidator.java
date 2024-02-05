package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void existsById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new DataValidationException("Проект с id - " + id + " не существует");
        }
    }
}