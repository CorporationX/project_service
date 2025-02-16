package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.exception.StageDeletionException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MoveTasksToAnotherStageStrategyTest {

    private MoveTasksToAnotherStageStrategy moveTasksToAnotherStageStrategy;
    private Stage stage;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        moveTasksToAnotherStageStrategy = new MoveTasksToAnotherStageStrategy();
        stage = new Stage();
        task1 = new Task();
        task2 = new Task();
        stage.setTasks(List.of(task1, task2));
    }

    @Test
    void handleTasksBeforeStageDeletion_shouldAllowDeletionIfAllTasksDoneOrCancelled() {
        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.CANCELLED);

        assertDoesNotThrow(() -> moveTasksToAnotherStageStrategy.handleTasksBeforeStageDeletion(stage));
    }

    @Test
    void handleTasksBeforeStageDeletion_shouldThrowExceptionIfTasksAreNotCompleted() {

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);


        StageDeletionException exception = assertThrows(StageDeletionException.class,
                () -> moveTasksToAnotherStageStrategy.handleTasksBeforeStageDeletion(stage));

        assertTrue(exception.getMessage().contains("Need to move, cancel or delete uncompleted tasks"));
    }

    @Test
    void handleTasksBeforeStageDeletion_shouldDoNothingIfNoTasks() {
        Stage emptyStage = new Stage();

        assertDoesNotThrow(() -> moveTasksToAnotherStageStrategy.handleTasksBeforeStageDeletion(emptyStage));
    }

    @Test
    void getName() {
        assertEquals(StageDeletionType.MOVE_TASKS, moveTasksToAnotherStageStrategy.getName());
    }
}