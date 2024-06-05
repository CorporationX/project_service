package faang.school.projectservice.pattern.strategy.stage;

import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CASCADE")
@RequiredArgsConstructor
public class StrategyCascadeForDeletingStage implements StrategyForDeletingStage {
    private final TaskRepository taskRepository;
    private final StageValidator stageValidator;

    @Override
    public void manageTasksOfStage(long stageId, Long stageToMigrateId) {
        stageValidator.validateStageExistence(stageId);
        taskRepository.deleteAllByStageId(stageId);
    }
}
