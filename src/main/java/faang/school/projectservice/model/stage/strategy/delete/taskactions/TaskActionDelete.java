package faang.school.projectservice.model.stage.strategy.delete.taskactions;

import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategy;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategyAbstract;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskActionDelete extends DeleteStageStrategyAbstract {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long stageId, Long unusedParameter) {
        stageRepository.deleteAllTasksByStageId(stageId);
        logAction("All tasks was deleted");
    }

    @Override
    public DeleteStageStrategy getMethod() {
        return DeleteStageStrategy.DELETE;
    }
}
