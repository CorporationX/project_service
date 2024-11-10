package faang.school.projectservice.deletestrategy;

import faang.school.projectservice.dto.stage.DeleteTypeDto;
import faang.school.projectservice.entity.stage.DeleteStrategy;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeleteWithClosingTasks extends DeleteStrategyExecutor {
    private final TaskRepository taskRepository;

    public DeleteWithClosingTasks(StageRepository stageRepository,
                                  TaskRepository taskRepository) {
        super(stageRepository, DeleteStrategy.CLOSE_TASKS);
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute(Stage stage, DeleteTypeDto deleteTypeDto) {
        stage.getTasks().forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
            task.setStage(null);
        });
        taskRepository.saveAll(stage.getTasks());
        log.info("{} tasks closed in stage: {}", stage.getTasks().size(), stage.getStageId());
        super.stageRepository.delete(stage);
    }
}