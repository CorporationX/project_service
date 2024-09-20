package faang.school.projectservice.model.stage.strategy.delete.taskactions;

import faang.school.projectservice.model.stage.strategy.delete.DeleteStageTaskStrategy;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategyExecutor;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CloseTaskStrategy extends DeleteStageStrategyExecutor {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long stageId, Long unusedParameter ) {
        stageRepository.closeTasksByStageId(stageId);
        logAction("All tasks was closed");
    }

    @Override
    public DeleteStageTaskStrategy getMethod() {
        return DeleteStageTaskStrategy.CLOSE;
    }
}
