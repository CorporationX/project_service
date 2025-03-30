package school.faang.projectservice.service;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.exception.TaskNotFoundException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.TaskServiceImpl;
import faang.school.projectservice.validator.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskValidator taskValidator;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private Task parentTask;
    private TaskDto taskDto;
    private TaskResponseDto responseDto;
    private final long validUserId = 1L;

    @BeforeEach
    void setUp() {
        parentTask = Task.builder()
                .id(3L)
                .name("Parent Task")
                .build();

        task = Task.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(validUserId)
                .parentTask(parentTask)
                .build();

        taskDto = TaskDto.builder()
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(validUserId)
                .parentTaskId(3L)
                .build();

        responseDto = TaskResponseDto.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(validUserId)
                .build();

        when(taskValidator.validateUserParticipationAndGetUserId()).thenReturn(validUserId);
    }

    @Test
    void testCreateTaskWithValidData_ReturnsTaskResponseDto() {
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskRepository.findById(3L)).thenReturn(Optional.of(parentTask));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.createTask(taskDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(taskRepository).findById(3L);
        verify(taskRepository).save(task);
        verify(taskMapper).toEntity(taskDto);
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    void testUpdateTaskWithExistingTask_ReturnsUpdatedTask() {
        Long parentTaskId = 3L;
        List<Long> linkedTaskIds = List.of(2L);
        Task linkedTask = Task.builder().id(2L).build();

        taskDto.setParentTaskId(parentTaskId);
        taskDto.setLinkedTaskIds(linkedTaskIds);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(linkedTask));
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);
        when(taskValidator.validateUserParticipationAndGetUserId()).thenReturn(validUserId);

        TaskResponseDto result = taskService.updateTask(1L, taskDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(taskValidator).validateUserParticipationAndGetUserId();
        verify(taskRepository).findById(1L);
        verify(taskRepository).findById(parentTaskId);
        verify(taskRepository).findById(2L);
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    void testUpdateTaskWithNonExistingTask_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, taskDto));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testGetTaskByIdWithExistingTask_ReturnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(taskRepository).findById(1L);
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    void testGetTaskByIdWithNonExistingTask_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository).findById(1L);
    }

    @Test
    void testGetFilteredTasks_ReturnsFilteredTasks() {
        TaskFilterDto filterDto = new TaskFilterDto("TODO", validUserId, "Test");
        List<Task> tasks = List.of(task);
        List<TaskResponseDto> expected = List.of(responseDto);

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toResponseDtoList(tasks)).thenReturn(expected);

        List<TaskResponseDto> result = taskService.getFilteredTasks(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, result);
        verify(taskRepository).findAll();
        verify(taskMapper).toResponseDtoList(tasks);
    }

    @Test
    void testDeleteExistingTask_DeletesTask() {
        when(taskValidator.validateUserParticipationAndGetUserId()).thenReturn(validUserId);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskValidator).validateUserParticipationAndGetUserId();
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void testDeleteNonExistingTask_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, never()).deleteById(any());
    }
}
