package faang.school.projectservice.model.stage.strategy.delete;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DeleteStageStrategyAbstract {
    public abstract void execute(Long providerStageId, Long consumerStageId);
    public abstract DeleteStageStrategy getMethod();

    protected void logAction(String message) {
        log.info(message);
    }


}


