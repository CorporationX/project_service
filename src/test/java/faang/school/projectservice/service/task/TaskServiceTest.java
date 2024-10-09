package faang.school.projectservice.service.task;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final List<Long> IDS = new ArrayList<>();
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    @InjectMocks
    private TaskService taskService;
    @Mock
    private TaskRepository taskRepository;
    private Task taskOne;
    private Task taskTwo;


    @Nested
    class PositiveTest {

        @BeforeEach
        void init() {
            IDS.add(ID_ONE);
            IDS.add(ID_TWO);

            taskOne = Task.builder()
                    .id(ID_ONE)
                    .build();

            taskTwo = Task.builder()
                    .id(ID_TWO)
                    .build();
        }

        @Test
        @DisplayName("Returning a list of all users by ID")
        void whenAppealDbThenReturnAllById() {
            List<Task> tasks = new ArrayList<>(Arrays.asList(taskOne, taskTwo));

            when(taskRepository.findAllById(IDS)).thenReturn(tasks);

            List<Task> result = taskService.getAllById(IDS);

            assertNotNull(result);
            assertEquals(result, tasks);

            verify(taskRepository).findAllById(IDS);
        }
    }
}