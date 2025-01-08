package faang.school.projectservice.service.task;

import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskJpaRepository taskJpaRepository;

    @InjectMocks
    private TaskService taskService;

    private List<Task> tasks;

    @BeforeEach
    public void setUp() {
        tasks = List.of(
                new Task(),
                new Task()
        );
    }

//    @Test
//    @DisplayName("Проверка saveAll - все tasks сохранены")
//    public void testSaveAll_saveAllTasks() {
//        taskService.saveAll(tasks);
//        verify(taskJpaRepository, times(1)).saveAll(tasks);
//    }

}
