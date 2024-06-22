package faang.school.projectservice.pattern.strategy.stage;

import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validation.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("MIGRATE")
@RequiredArgsConstructor
public class StrategyMigrateForDeletingStage implements StrategyForDeletingStage {
    private final TaskRepository taskRepository;
    private final StageValidator stageValidator;

    @Override
    public void manageTasksOfStage(long stageId, Long stageToMigrateId) {
        stageValidator.validateStageExistence(stageId);
        stageValidator.validateStageForToMigrateExistence(stageToMigrateId);
        taskRepository.updateStageId(stageId, stageToMigrateId);
    }
}
