package faang.school.projectservice.testservice;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.exception.TaskEntityNotFoundException;
import faang.school.projectservice.exception.TaskValidationException;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Spy // Используем реальную реализацию маппера
    private TaskMapperImpl taskMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private TaskService taskService;

    private TaskRequestDto taskRequestDto;
    private Project project;
    private Stage stage;
    private Long projectId = 1L;
    private Long reporterUserId = 4L;
    private Long taskId = 1L;

    @BeforeEach
    void setUp() {
        taskRequestDto = new TaskRequestDto(
                "Test Task",
                "Test Description",
                TaskStatus.OPEN,
                3L,
                reporterUserId,
                0,
                null,
                null,
                projectId,
                1L
        );

        project = new Project();
        project.setId(projectId);

        stage = new Stage();
        stage.setStageId(1L);
    }

    @Test
    void createTaskShouldReturnTaskDto() {
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(userContext.getUserId()).thenReturn(reporterUserId);
        Stage stage = Stage.builder().stageId(1L).build();

        when(taskMapper.toEntity(taskRequestDto)).thenReturn(
                Task.builder()
                        .name("Test Task")
                        .description("Test Description")
                        .status(TaskStatus.OPEN)
                        .performerUserId(3L)
                        .reporterUserId(reporterUserId)
                        .minutesTracked(0)
                        .project(project)
                        .stage(stage)
                        .build()
        );

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(taskId);
            return savedTask;
        });

        TaskDto result = taskService.createTask(taskRequestDto, projectId);

        assertNotNull(result);
        assertEquals("Test Task", result.name());
        assertEquals("Test Description", result.description());
        assertEquals(TaskStatus.OPEN, result.status());
        assertEquals(3L, result.performerUserId());
        assertEquals(reporterUserId, result.reporterUserId());
        assertEquals(0, result.minutesTracked());
        assertEquals(projectId, result.projectId());
        assertEquals(1L, result.stageId());
        verify(taskRepository).save(any(Task.class));
        verify(projectService).getProjectById(projectId);
        verify(userContext).getUserId();
    }

    @Test
    void createTaskShouldThrowExceptionWhenStageIdIsNull() {
        TaskRequestDto invalidTaskRequestDto = new TaskRequestDto(
                "Test Task",
                "Test Description",
                TaskStatus.OPEN,
                3L,
                reporterUserId,
                0,
                null,
                null,
                1L,
                null
        );

        Exception exception = assertThrows(TaskValidationException.class, () ->
                taskService.createTask(invalidTaskRequestDto, 1L));

        assertEquals("StageId can't be null", exception.getMessage());
    }

    @Test
    void updateTaskShouldReturnUpdatedTaskDto() {
        // Arrange
        Task existingTask = Task.builder()
                .id(taskId)
                .name("Original Task")
                .description("Original Description")
                .status(TaskStatus.OPEN)
                .performerUserId(5L)
                .reporterUserId(reporterUserId)
                .minutesTracked(5)
                .project(project)
                .stage(stage)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userContext.getUserId()).thenReturn(reporterUserId);
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskDto result = taskService.updateTask(taskId, taskRequestDto);

        assertNotNull(result);
        assertEquals("Test Task", result.name());
        assertEquals("Test Description", result.description());
        assertEquals(TaskStatus.OPEN, result.status());
        assertEquals(3L, result.performerUserId());
        assertEquals(reporterUserId, result.reporterUserId());
        assertEquals(0, result.minutesTracked());
        assertEquals(projectId, result.projectId());
        assertEquals(1L, result.stageId());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
        verify(userContext).getUserId();
    }

    @Test
    void updateTaskShouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TaskEntityNotFoundException.class, () ->
                taskService.updateTask(taskId, taskRequestDto));

        assertEquals("Task with id 1 not found", exception.getMessage());
    }

    @Test
    void getAllTasksByProjectIdWithFiltersShouldReturnListOfTaskDtos() {

        Task task = Task.builder()
                .id(taskId)
                .name("Filtered Task")
                .description("Filtered Description")
                .status(TaskStatus.OPEN)
                .performerUserId(3L)
                .reporterUserId(reporterUserId)
                .minutesTracked(15)
                .project(project)
                .stage(stage)
                .build();

        when(taskRepository.findTasksByFilters(eq(projectId), eq(TaskStatus.OPEN), eq(3L), eq("keyword")))
                .thenReturn(Collections.singletonList(task));

        List<TaskDto> result = taskService.getAllTasksByProjectIdWithFilters(projectId, TaskStatus.OPEN,
                3L, "keyword");

        assertEquals(1, result.size());
        assertEquals("Filtered Task", result.get(0).name());
        verify(taskRepository).findTasksByFilters(eq(projectId), eq(TaskStatus.OPEN), eq(3L),
                eq("keyword"));
    }

    @Test
    void getAllTasksByProjectIdShouldReturnListOfTaskDtos() {

        Task task = Task.builder()
                .id(taskId)
                .name("Project Task")
                .description("Project Description")
                .status(TaskStatus.OPEN)
                .performerUserId(3L)
                .reporterUserId(reporterUserId)
                .minutesTracked(20)
                .project(project)
                .stage(stage)
                .build();

        when(taskRepository.findAllByProjectId(projectId))
                .thenReturn(Collections.singletonList(task));

        List<TaskDto> result = taskService.getAllTasksByProjectId(projectId);

        assertEquals(1, result.size());
        assertEquals("Project Task", result.get(0).name());
        verify(taskRepository).findAllByProjectId(projectId);
    }

    @Test
    void getAllTasksByProjectIdShouldThrowExceptionWhenNoTasksFound() {
        when(taskRepository.findAllByProjectId(projectId))
                .thenReturn(Collections.emptyList());
        Exception exception = assertThrows(TaskEntityNotFoundException.class, () ->
                taskService.getAllTasksByProjectId(projectId));

        assertEquals("Tasks not found for project with id: 1", exception.getMessage());

        verify(taskRepository).findAllByProjectId(projectId);
    }

    @Test
    void getTaskByIdShouldReturnTaskDto() {

        Task task = Task.builder()
                .id(taskId)
                .name("Task to Get")
                .description("Description to Get")
                .status(TaskStatus.OPEN)
                .performerUserId(3L)
                .reporterUserId(reporterUserId)
                .minutesTracked(25)
                .project(project)
                .stage(stage)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskDto result = taskService.getTaskById(taskId);

        assertEquals("Task to Get", result.name());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void getTaskByIdShouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskEntityNotFoundException exception = assertThrows(TaskEntityNotFoundException.class, () ->
                taskService.getTaskById(taskId));

        assertEquals("Task not found with id: 1", exception.getMessage());

        verify(taskRepository).findById(taskId);
    }
}