package faang.school.projectservice.service.stage.executor;

import faang.school.projectservice.model.stage.strategy.DeleteStageTaskStrategy;
import faang.school.projectservice.model.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DeleteStageProcessor {
    private final Map<DeleteStageTaskStrategy, DeleteStageStrategyExecutor> taskActionMap;

    @Autowired
    public DeleteStageProcessor(List<DeleteStageStrategyExecutor> taskActions) {
        this.taskActionMap = taskActions.stream().collect(Collectors.toMap(
                DeleteStageStrategyExecutor::getMethod,
                taskAction -> taskAction
        ));
    }

    public void process(Stage stage, DeleteStageTaskStrategy actionType, Long consumerStageId) {
        DeleteStageStrategyExecutor taskAction = getTaskAction(actionType);
        taskAction.execute(stage.getStageId(), consumerStageId);
        log.info("Tasks have been {} from stage with id {}", actionType, stage.getStageId());
    }

    public DeleteStageStrategyExecutor getTaskAction(DeleteStageTaskStrategy actionType) {
        return taskActionMap.get(actionType);
    }


}