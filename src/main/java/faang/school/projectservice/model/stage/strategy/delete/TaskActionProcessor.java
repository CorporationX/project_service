package faang.school.projectservice.model.stage.strategy.delete;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.strategy.delete.taskactions.TaskActionClose;
import faang.school.projectservice.model.stage.strategy.delete.taskactions.TaskActionDelete;
import faang.school.projectservice.model.stage.strategy.delete.taskactions.TaskActionReassign;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TaskActionProcessor {

    private final Map<DeleteStageStrategy, DeleteStageStrategyAbstract> taskActionMap;

    @Autowired
    public TaskActionProcessor(List<DeleteStageStrategyAbstract> taskActions) {
        this.taskActionMap = taskActions.stream().collect(Collectors.toMap(
                taskAction -> {
                    DeleteStageStrategy method = taskAction.getMethod();
                    if (method == null) {
                        throw new IllegalArgumentException("Task action method is null");
                    }
                    return method;
                },
                taskAction -> taskAction
        ));
    }

    public void process(Stage stage, DeleteStageStrategy actionType, Long consumerStageId) {
        DeleteStageStrategyAbstract taskAction = taskActionMap.get(actionType);
        if (taskAction != null) {
            taskAction.execute(stage.getStageId(), consumerStageId);
        } else {
            throw new IllegalArgumentException("No handler found for action: " + actionType);
        }
    }
}