package faang.school.projectservice.pattern.strategy.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validation.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CLOSE")
@RequiredArgsConstructor
public class StrategyCloseForDeletingStage implements StrategyForDeletingStage {
    private final TaskRepository taskRepository;
    private final StageValidator stageValidator;

    @Override
    public void manageTasksOfStage(long stageId, Long stageToMigrateId) {
        stageValidator.validateExistence(stageId);
        taskRepository.updateStatusByStageId(stageId, TaskStatus.DONE);
    }
}
