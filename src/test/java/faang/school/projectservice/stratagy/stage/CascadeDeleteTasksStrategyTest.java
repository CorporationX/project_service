package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CascadeDeleteTasksStrategyTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CascadeDeleteTasksStrategy cascadeDeleteTasksStrategy;

    private Stage stage;

    @BeforeEach
    void setUp() {
        stage = new Stage();
        stage.setTasks(List.of(new Task(), new Task())); // Добавляем 2 задачи
    }

    @Test
    void handleTasksBeforeStageDeletion_shouldDeleteAllTasks() {
        cascadeDeleteTasksStrategy.handleTasksBeforeStageDeletion(stage);

        verify(taskRepository, times(1)).deleteAll(stage.getTasks());
    }

    @Test
    void getName_shouldReturnCascadeDelete() {
        assertEquals(StageDeletionType.CASCADE_DELETE, cascadeDeleteTasksStrategy.getName());
    }
}