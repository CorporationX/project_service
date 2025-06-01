package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.task.TaskFilterStrategy;
import faang.school.projectservice.filter.task.TaskKeyWordFilter;
import faang.school.projectservice.filter.task.TaskPerformerFilter;
import faang.school.projectservice.filter.task.TaskStatusFilter;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.mapper.TaskMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.task.TaskService;
import faang.school.projectservice.service.task.TaskServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskKeyWordFilter taskKeyWordFilter;
    @Mock
    private TaskStatusFilter taskStatusFilter;
    @Mock
    private TaskPerformerFilter taskPerformerFilter;
    private final TaskMapper taskMapper = new TaskMapperImpl();
    @Mock
    private UserContext userContext;
    @Spy
    private List<TaskFilterStrategy> taskFilterStrategies;
    @Mock
    private ProjectRepository projectRepository;

    private TaskService taskService;


    Task task = Task.builder().description("123").status(TaskStatus.TESTING).performerUserId(1L)
            .project(Project.builder().id(1L).build()).build();
    Task matchingTask = Task.builder().description("222").status(TaskStatus.IN_PROGRESS).performerUserId(10L).build();
    Task matchingTask2 = Task.builder().description("222").status(TaskStatus.IN_PROGRESS).performerUserId(10L).build();
    TaskFilterDto filterDto = new TaskFilterDto("222", TaskStatus.IN_PROGRESS, 10L);
    List<TeamMember> teamMembers = List.of(TeamMember.builder().id(1L).build());
    List<Team> teams = List.of(Team.builder().id(1L).teamMembers(teamMembers).build());
    List<Task> tasks = List.of(task, matchingTask, matchingTask2);
    Project project = Project.builder().id(1L).tasks(tasks).teams(teams).build();
    TaskDto taskDto = TaskDto.builder().id(1L).name("test").project(project).build();


    @BeforeEach
    void setUp() {
        taskFilterStrategies = List.of(
                taskKeyWordFilter,
                taskPerformerFilter,
                taskStatusFilter
        );
        taskService = new TaskServiceImpl(
                taskRepository,
                taskMapper, userContext,
                taskFilterStrategies,
                projectRepository
        );
    }

    @Test
    public void test_createTask_when_NullProject() {
        taskDto.setProject(null);
        Assertions.assertThrows(DataValidationException.class, () -> taskService.createTask(taskDto));
    }

    @Test
    public void test_createTask_when_NullTask() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.createTask(null));
    }

    @Test
    public void test_createTask_ShouldSaveTask() {
        taskService.createTask(taskDto);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    public void test_updateTask_when_NoSuchId() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(1L, taskDto));
    }

    @Test
    public void test_updateTask_shouldSaveTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        taskService.updateTask(1L, taskDto);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    public void test_getAllTasks_when_NotWorkingOnProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(10L);
        Assertions.assertThrows(DataValidationException.class,
                () -> taskService.getAllTasks(1L, null));
    }

    @Test
    public void test_getAllTasks_when_FilterDtoIsNull() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(1L);
        when(taskRepository.findAllByProjectId(1L)).thenReturn(tasks);
        List<TaskDto> result = taskService.getAllTasks(1L, null);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void test_getAllTasks_when_FilterDtoIsNotNull() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(1L);
        when(taskRepository.findAllByProjectId(1L)).thenReturn(tasks);

        when(taskKeyWordFilter.isAppicable(filterDto)).thenReturn(false);
        when(taskPerformerFilter.isAppicable(filterDto)).thenReturn(true);
        when(taskStatusFilter.isAppicable(filterDto)).thenReturn(false);

        when(taskPerformerFilter.filter(task, filterDto)).thenReturn(false);
        when(taskPerformerFilter.filter(matchingTask, filterDto)).thenReturn(true);
        when(taskPerformerFilter.filter(matchingTask2, filterDto)).thenReturn(true);
        List<TaskDto> result = taskService.getAllTasks(1L, filterDto);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void test_getTaskById_when_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,()-> taskService.getTask(1L));
    }

    @Test
    public void test_getTaskById_when_TaskFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(1L);
        Assertions.assertEquals(task.getId(), taskService.getTask(1L).getId());
    }

}
