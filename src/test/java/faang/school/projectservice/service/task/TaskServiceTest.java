package faang.school.projectservice.service.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.filter.TaskFilterDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.task.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskValidator taskValidator;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService taskService;

    private UserDto userDto;
    private TaskDto taskDto;
    private Task task;

    private Project project;

    private Long taskId;

    private Long userId;

    private Long projectId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        taskId = 1L;

        userId = 1L;

        projectId = 1L;

        project = new Project();
        project.setId(projectId);

        userDto = new UserDto();
        userDto.setId(userId);

        task = new Task();
        task.setProject(project);

        taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setReporterUserId(1L);
        taskDto.setPerformerUserId(1L);
        taskDto.setProjectId(1L);
        taskDto.setName("task");
        taskDto.setStatus("TODO");
        taskDto.setLinkedTasksIds(List.of());
    }

    @Test
    public void testCreateTask() {
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);
        when(projectRepository.getProjectById(anyLong())).thenReturn(new Project());

        TaskDto result = taskService.createTask(taskDto);

        assertEquals(taskDto, result);
        verify(taskValidator, times(2)).validateTeamMember(anyLong(), anyLong());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTask() {
        when(taskValidator.validateTask(taskId)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(taskId, taskDto);

        assertEquals(taskDto, result);
        verify(taskValidator, times(1)).validateTask(taskId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testGetTaskById() {
        when(taskValidator.validateTask(taskId)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        TaskDto result = taskService.getTaskById(taskId, userId);

        assertEquals(taskDto, result);
        verify(taskValidator, times(1)).validateTask(taskId);
        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    public void testGetTasksByProjectId() {
        List<Task> tasks = Collections.singletonList(new Task());
        List<TaskDto> taskDtos = Collections.singletonList(taskDto);

        when(taskRepository.findAllByProjectId(project.getId())).thenReturn(tasks);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        List<TaskDto> result = taskService.getTasksByProjectId(project.getId(), userId);

        assertEquals(taskDtos, result);
        verify(projectService, times(1)).getProjectById(project.getId());
        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    public void testGetTasksByProjectIdAndFilter() {
        TaskFilterDto filters = new TaskFilterDto();
        List<Task> tasks = Collections.singletonList(new Task());
        List<TaskDto> taskDtos = List.of();

        when(taskRepository.findAllByProjectId(projectId)).thenReturn(tasks);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        List<TaskDto> result = taskService.getTasksByProjectIdAndFilter(projectId, userId, filters);

        assertEquals(taskDtos, result);
        verify(projectService, times(1)).getProjectById(projectId);
        verify(userServiceClient, times(1)).getUser(userId);
    }
}