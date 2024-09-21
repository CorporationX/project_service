package faang.school.projectservice.service.stage.executor;

import faang.school.projectservice.model.stage.strategy.DeleteStageTaskStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DeleteStageStrategyExecutor {
    public abstract DeleteStageTaskStrategy getMethod();

    public abstract void execute(Long providerStageId, Long consumerStageId);

    protected void logAction(String message) {
        log.info(message);
    }
}


