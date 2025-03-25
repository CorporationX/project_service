package faang.school.projectservice.service.Stage;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CloseTasksStrategy implements StageDeletionStrategy{
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;
    @Override
    public void deleteStage(Stage stage, Stage targetStage) { // targetStage игнорируется
        List<Task> tasks = taskRepository.findByStage(stage);
        tasks.forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        taskRepository.saveAll(tasks);
        log.info("Tasks have been closed, {}", tasks);
        log.info("Delete stage {}", stage);
        stageRepository.delete(stage);
    }

    @Override
    public boolean requiresTargetStage() {
        return false;
    }
}
