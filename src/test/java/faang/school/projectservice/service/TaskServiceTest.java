package faang.school.projectservice.service;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private TaskDto taskDto;
    private Task task;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task");
        taskDto.setDescription("Test Description");

        task = new Task();
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
    }

    @Test
    void createTask_Success() {
        when(taskMapper.toEntity(any(TaskDto.class))).thenReturn(task);
        taskService.createTask(taskDto);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        doNothing().when(taskMapper).updateTaskFromDto(any(TaskDto.class), any(Task.class));
        taskService.updateTask(taskDto);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_Failure() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> taskService.updateTask(taskDto));

        assertEquals("Такой задачи не существует", dataValidationException.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }
}
