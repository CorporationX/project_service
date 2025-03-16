package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void checkingExistenceProject(long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("Такого проекта не существует!");
        }
    }
}
