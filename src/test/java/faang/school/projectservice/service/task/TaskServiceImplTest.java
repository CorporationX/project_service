package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.exception.TaskNotFoundException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private TaskPermissionService taskPermissionService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskDto taskDto;
    private Task task;
    private TaskResponseDto taskResponseDto;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .parentTaskId(null)
                .linkedTaskIds(null)
                .projectId(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        task = Task.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(1L)
                .project(null)
                .build();

        taskResponseDto = TaskResponseDto.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .projectId(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void testCreateTask() {
        when(taskPermissionService.validateTaskAccess()).thenReturn(1L);
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.createTask(taskDto);

        assertNotNull(result);
        assertEquals(taskResponseDto, result);
        verify(taskPermissionService, times(1)).validateTaskAccess();
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testUpdateTask() throws Exception {
        Task updatedTask = task.toBuilder()
                .name("Updated Task")
                .description("Updated Description")
                .build();

        TaskDto updatedDto = taskDto.builder()
                .name("Updated Task")
                .description("Updated Description")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toResponseDto(any(Task.class))).thenReturn(taskResponseDto.builder()
                .name("Updated Task")
                .description("Updated Description")
                .build());

        TaskResponseDto result = taskService.updateTask(1L, updatedDto);

        assertNotNull(result);
        assertEquals("Updated Task", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testGetFilteredTasks() {
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(TaskStatus.TODO)
                .performerId(1L)
                .keyword("test")
                .build();

        when(taskRepository.findFilteredTasks(TaskStatus.TODO, 1L, "test"))
                .thenReturn(List.of(task));
        when(taskMapper.toResponseDtoList(List.of(task))).thenReturn(List.of(taskResponseDto));

        List<TaskResponseDto> result = taskService.getFilteredTasks(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskResponseDto, result.get(0));
        verify(taskRepository, times(1))
                .findFilteredTasks(TaskStatus.TODO, 1L, "test");
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponseDtoList(List.of(task))).thenReturn(List.of(taskResponseDto));

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskResponseDto, result.get(0));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTasksByProjectId() {
        when(taskRepository.findAllByProjectId(1L)).thenReturn(List.of(task));
        when(taskMapper.toResponseDtoList(List.of(task))).thenReturn(List.of(taskResponseDto));

        List<TaskResponseDto> result = taskService.getAllTasksByProjectId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskResponseDto, result.get(0));
        verify(taskRepository, times(1)).findAllByProjectId(1L);
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(taskResponseDto, result);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteTask() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskPermissionService).validateTaskDeletePermission(task);
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskPermissionService, times(1)).validateTaskDeletePermission(task);
        verify(taskRepository, times(1)).delete(task);
    }
}