package faang.school.projectservice.controller;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Mock
    private TaskService taskService;
    @InjectMocks
    private TaskController taskController;

    @Test
    void testCreateTaskCallsServiceMethod() {
        TaskDto taskDto = TaskDto.builder().build();
        taskController.createTask(taskDto);
        verify(taskService,times(1)).createTask(taskDto);
    }

    @Test
    void testUpdateTaskCallsServiceMethod() {
        TaskDto taskDto = TaskDto.builder().build();
        taskController.updateTask(taskDto);
        verify(taskService,times(1)).updateTask(taskDto);
    }
}