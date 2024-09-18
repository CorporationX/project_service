package faang.school.projectservice.model.stage.strategy.delete.taskactions;

import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategy;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategyAbstract;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskActionClose extends DeleteStageStrategyAbstract {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long stageId, Long unusedParameter ) {
        stageRepository.closeTasksByStageId(stageId);
        logAction("All tasks was closed");
    }

    @Override
    public DeleteStageStrategy getMethod() {
        return DeleteStageStrategy.CLOSE;
    }
}
