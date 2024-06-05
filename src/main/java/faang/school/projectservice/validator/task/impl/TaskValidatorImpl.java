package faang.school.projectservice.validator.task.impl;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.validator.task.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskValidatorImpl implements TaskValidator {
    private final TaskRepository taskRepository;


    @Override
    public Task validateTaskExistence(long id) {
        var optional = taskRepository.findById(id);
        return optional.orElseThrow(() -> {
            var message = String.format("a task with %d does not exist", id);

            return new DataValidationException(message);
        });
    }
}
