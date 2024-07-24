package faang.school.projectservice.valitator.initiative;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InitiativeStatusDoneValidatorTest {

    private InitiativeStatusDoneValidator validator = new InitiativeStatusDoneValidator();

    @Test
    void checkCompletedStagesSuccess() {
        Initiative initiative = getInitiativeWithTaskStatusDone();
        assertDoesNotThrow(() -> validator.checkCompletedStages(initiative));
    }

    @Test
    void checkCompletedStagesUnsuccess() {
        Task taskFailed = new Task();
        taskFailed.setStatus(TaskStatus.IN_PROGRESS);
        Initiative initiative = getInitiativeWithTaskStatusDone();
        initiative.getStages().get(0).getTasks().add(taskFailed);

        assertThrows(RuntimeException.class, () -> validator.checkCompletedStages(initiative));
    }

    private static Initiative getInitiativeWithTaskStatusDone() {
        Task task11 = new Task();
        task11.setStatus(TaskStatus.DONE);
        Task task12 = new Task();
        task12.setStatus(TaskStatus.DONE);
        Task task21 = new Task();
        task21.setStatus(TaskStatus.DONE);
        Stage stage1 = new Stage();
        stage1.setTasks(new ArrayList<>(List.of(task11, task12)));
        Stage stage2 = new Stage();
        stage2.setTasks(List.of(task21));
        Initiative initiative = new Initiative();
        initiative.setStages(List.of(stage1, stage2));
        return initiative;
    }
}