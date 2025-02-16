package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseTasksStrategyTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CloseTasksStrategy closeTasksStrategy;

    private Stage stage;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setStatus(TaskStatus.IN_PROGRESS);

        task2 = new Task();
        task2.setStatus(TaskStatus.DONE);

        stage = new Stage();
        stage.setTasks(List.of(task1, task2));
    }

    @Test
    void handleTasksBeforeStageDeletion() {
        closeTasksStrategy.handleTasksBeforeStageDeletion(stage);

        assertEquals(TaskStatus.CANCELLED, task1.getStatus());
        assertEquals(TaskStatus.CANCELLED, task2.getStatus());

        verify(taskRepository, times(1)).saveAll(stage.getTasks());
    }

    @Test
    void handleTasksBeforeStageDeletion_shouldDoNothingIfNoTasks() {
        Stage emptyStage = new Stage();
        closeTasksStrategy.handleTasksBeforeStageDeletion(emptyStage);

        verify(taskRepository, never()).saveAll(any());
    }

    @Test
    void getName() {
        assertEquals(StageDeletionType.CLOSE_TASKS, closeTasksStrategy.getName());
    }
}