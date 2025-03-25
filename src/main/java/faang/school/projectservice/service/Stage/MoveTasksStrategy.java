package faang.school.projectservice.service.Stage;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MoveTasksStrategy implements StageDeletionStrategy {
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;

    @Override
    public void deleteStage(Stage stage, Stage targetStage) {
        if (targetStage == null) {
            throw new IllegalArgumentException("Target stage must be specified for MoveTasksStrategy.");
        }
        List<Task> tasks = taskRepository.findByStage(stage);
        tasks.forEach(task -> task.setStage(targetStage));
        taskRepository.saveAll(tasks);
        log.info("Tasks have been moved: {} \n to stage: {}", tasks, targetStage);
        log.info("Delete stage: {}", stage);
        stageRepository.delete(stage);
    }

    @Override
    public boolean requiresTargetStage() {
        return true;
    }
}
