package faang.school.projectservice.service.project.stage.remove;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static faang.school.projectservice.exception.ExceptionMessages.TASKS_NOT_FOUND;

@Component
public class RemoveWithClosingTasks extends RemoveStrategyExecutor {
    private final TaskRepository taskRepository;

    public RemoveWithClosingTasks(StageRepository stageRepository,
                                  TaskRepository taskRepository,
                                  RemoveStrategy removeStrategy) {
        super(stageRepository, removeStrategy);
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        if (stage.getTasks() == null) {
            throw new EntityNotFoundException(TASKS_NOT_FOUND.getMessage().formatted(stage.getStageId()));
        }
        stage.getTasks().forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
            task.setStage(null);
        });
        taskRepository.saveAll(stage.getTasks());
        super.stageRepository.delete(stage);
    }
}
