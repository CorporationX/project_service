package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.exception.StageDeletionException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MoveTasksToAnotherStageStrategy implements StageDeletionStrategy {

    @Override
    public void handleTasksBeforeStageDeletion(Stage stage) {
        List<Task> tasks = stage.getTasks();
        if (CollectionUtils.isNotEmpty(tasks)) {
            if (!tasks.stream()
                    .filter(Objects::nonNull)
                    .map(Task::getStatus)
                    .allMatch(status -> status == TaskStatus.DONE || status == TaskStatus.CANCELLED)) {
                throw new StageDeletionException("Need to move, cancel or delete uncompleted " +
                        "tasks from stage " + stage.getStageId());
            }
        }
    }

    @Override
    public StageDeletionType getName() {
        return StageDeletionType.MOVE_TASKS;
    }
}
