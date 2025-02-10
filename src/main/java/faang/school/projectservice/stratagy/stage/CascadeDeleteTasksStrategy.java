package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CascadeDeleteTasksStrategy implements StageDeletionStrategy {
    private final TaskRepository taskRepository;

    @Override
    public void handleTasksBeforeStageDeletion(Stage stage) {
        taskRepository.deleteAll(stage.getTasks());
    }

    @Override
    public StageDeletionType getName() {
        return StageDeletionType.CASCADE_DELETE;
    }
}