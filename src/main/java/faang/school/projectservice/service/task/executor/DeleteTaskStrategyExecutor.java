package faang.school.projectservice.service.task.executor;

import faang.school.projectservice.model.stage.strategy.DeleteStageTaskStrategy;
import faang.school.projectservice.service.stage.executor.DeleteStageStrategyExecutor;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteTaskStrategyExecutor extends DeleteStageStrategyExecutor {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long stageId, Long unusedParameter) {
        stageRepository.deleteAllTasksByStageId(stageId);
        logAction("All tasks was deleted");
    }

    @Override
    public DeleteStageTaskStrategy getMethod() {
        return DeleteStageTaskStrategy.DELETE;
    }
}
