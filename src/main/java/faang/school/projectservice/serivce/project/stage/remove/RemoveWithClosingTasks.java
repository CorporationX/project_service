package faang.school.projectservice.serivce.project.stage.remove;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.springframework.stereotype.Component;

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
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        stage.getTasks().forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
            task.setStage(null);
        });
        taskRepository.saveAll(stage.getTasks());
        super.stageRepository.delete(stage);
    }
}
