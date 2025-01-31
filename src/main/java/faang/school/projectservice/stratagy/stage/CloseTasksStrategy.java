package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CloseTasksStrategy implements StageDeletionStrategy {
    private final TaskRepository taskRepository;

    @Override
    public void handleTasksBeforeStageDeletion(Stage stage) {
        List<Task> tasks = stage.getTasks();
        if (CollectionUtils.isNotEmpty(tasks)) {
            tasks.stream()
                    .filter(Objects::nonNull)
                    .forEach(task -> task.setStatus(TaskStatus.CANCELLED));
            taskRepository.saveAll(stage.getTasks());
        }
    }

    @Override
    public StageDeletionType getName() {
        return StageDeletionType.CLOSE_TASKS;
    }
}