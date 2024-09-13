package faang.school.projectservice.controller.validation;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskStatusValidator {
    public void validateIsItTaskStatus(TaskStatus taskStatus) {
        if (taskStatus == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }
        if(!TaskStatus.getAll().contains(taskStatus)){
            throw new IllegalArgumentException("Invalid task status: " + taskStatus);
        }
    }
}
