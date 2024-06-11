package faang.school.projectservice.validator.project.impl;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidatorImpl implements ProjectValidator {
    private final ProjectRepository projectRepository;

    @Override
    public void validateProjectExistence(long id) {
        boolean exists = projectRepository.existsById(id);
        if (!exists) {
            String message = String.format("a project with id %d does not exist", id);

            throw new DataValidationException(message);
        }
    }
}
