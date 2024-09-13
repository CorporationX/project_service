package faang.school.projectservice.controller.validation;

import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionAfterDeletingStage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskActionsAfterDeletingStageValidator {

    public void validateIsItTaskActionsAfterDeletingStage(TaskActionAfterDeletingStage taskAction) {
        if (taskAction == null) {
            throw new IllegalArgumentException("Task actions after deleting stage cannot be null");
        }
        if(!TaskActionAfterDeletingStage.getAll().contains(taskAction)){
            throw new IllegalArgumentException("Invalid team role: " + taskAction);
        }
    }
}
