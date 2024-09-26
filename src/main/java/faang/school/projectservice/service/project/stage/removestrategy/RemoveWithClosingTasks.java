package faang.school.projectservice.service.project.stage.removestrategy;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RemoveWithClosingTasks extends RemoveStrategyExecutor {
    private final TaskRepository taskRepository;

    public RemoveWithClosingTasks(StageRepository stageRepository,
                                  TaskRepository taskRepository) {
        super(stageRepository, RemoveStrategy.CLOSE);
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        stage.getTasks().forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
            task.setStage(null);
        });
        taskRepository.saveAll(stage.getTasks());
        log.info("{} tasks closed in stage: {}", stage.getTasks().size(), stage.getStageId());
        super.stageRepository.delete(stage);
    }
}
