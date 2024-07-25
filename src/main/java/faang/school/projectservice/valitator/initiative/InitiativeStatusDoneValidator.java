package faang.school.projectservice.valitator.initiative;

import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitiativeStatusDoneValidator {

    public void checkCompletedStages(Initiative initiative) {
        for (Stage stage : initiative.getStages()) {
            for (Task task : stage.getTasks()) {
                if (!task.getStatus().equals(TaskStatus.DONE)) {
                    log.error("InitiativeStatusDoneValidator.checkCompletedStages: Unable to complete there are uncompleted stages! Task not done: []", task.getId());
                    throw new ConflictException("Unable to complete there are uncompleted stages! Task not done: " + task.getId());
                }
            }
        }
    }
}
