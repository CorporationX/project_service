package faang.school.projectservice.model.taskActionAfterDeletingStage;

import lombok.extern.slf4j.Slf4j;




@Slf4j
public abstract class TaskActionAfterDeletingStageAbstract {
    public abstract void execute(Long providerStageId, Long consumerStageId);

    protected void logAction(String message) {
        log.info(message);
    }
}


