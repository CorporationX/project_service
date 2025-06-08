package faang.school.projectservice.controller;

import faang.school.projectservice.controller.task.TaskController;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDto taskDto;
    private TaskFilterDto taskFilterDto;
    private final Long taskId = 1L;
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
                .id(taskId)
                .name("test")
                .description("Test Description")
                .build();

        taskFilterDto = new TaskFilterDto("222", TaskStatus.IN_PROGRESS, 1L);
    }

    @Test
    void testCreateTask_Success() {
        taskController.createTask(taskDto);
        verify(taskService).createTask(taskDto);
    }

    @Test
    void testUpdateTask_Success() {
        taskController.updateTask(taskId, taskDto);
        verify(taskService).updateTask(taskId, taskDto);
    }

    @Test
    void testGetAllTasks_Success() {
        taskController.findAll(projectId, taskFilterDto);
        verify(taskService).getAllTasks(projectId, taskFilterDto);
    }

    @Test
    void testGetTask_Success() {
        when(taskService.getTask(taskId)).thenReturn(taskDto);
        TaskDto result = taskController.getTask(taskId);
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskService).getTask(taskId);
    }
}
