package faang.school.projectservice.validation.task.impl;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validation.task.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskValidatorImpl implements TaskValidator {
    private final TaskRepository taskRepository;

    @Override
    public void validateTaskExistence(long id) {
        boolean exists = taskRepository.existsById(id);
        if (!exists) {
            var message = String.format("a task with %d does not exist", id);

            throw new DataValidationException(message);
        }
    }
}
