package faang.school.projectservice.validator;

import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateExistProjectById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new DataValidationException("Проект с id - " + id + " не существует");
        }
    }
}