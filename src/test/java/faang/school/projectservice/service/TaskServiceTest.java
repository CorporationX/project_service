package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.filter.DescriptionFilter;
import faang.school.projectservice.service.filter.PerformerUserFilter;
import faang.school.projectservice.service.filter.TaskFilter;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private List<TaskFilter> taskFilters;
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final StageRepository stageRepository = mock(StageRepository.class);
    private final UserServiceClient userServiceClient = mock(UserServiceClient.class);

    @Test
    @DisplayName("Create task: check name is blank")
    public void testCreateTaskNameIsBlank() {
        Task task = new Task();
        task.setName("");

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Create task: check user performer exist")
    public void testCreateTaskCheckUserPerformerExists() {
        Task task = new Task();
        task.setName("Not empty");
        UserDto userPerformerDto = new UserDto();
        userPerformerDto.setId(2L);
        Mockito.when(userServiceClient.getUser(userPerformerDto.getId())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Create task: check user reporter exist")
    public void testCreateTaskCheckUserReporterExists() {
        Task task = new Task();
        task.setName("Not empty");
        task.setPerformerUserId(1L);

        UserDto userReporterDto = new UserDto();
        userReporterDto.setId(2L);
        Mockito.when(userServiceClient.getUser(userReporterDto.getId())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Create task: check parent task")
    public void testCreateTaskCheckParentTask() {
        Task task = new Task();
        task.setName("Not empty");
        task.setPerformerUserId(1L);
        task.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        task.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(task.getParentTask().getId())).thenThrow();

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Create task: check project")
    public void testCreateTaskCheckProject() {
        Task task = new Task();
        task.setName("Not empty");
        task.setPerformerUserId(1L);
        task.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        task.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(task.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        task.setProject(project);
        Mockito.when(projectRepository.getByIdOrThrow(task.getProject().getId())).thenThrow();

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Create task: check stage")
    public void testCreateTaskCheckStage() {
        Task task = new Task();
        task.setName("Not empty");
        task.setPerformerUserId(1L);
        task.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        task.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(task.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        task.setProject(project);
        Mockito.when(projectRepository.getByIdOrThrow(task.getProject().getId())).thenReturn(project);

        Stage stage = new Stage();
        stage.setStageId(3L);
        task.setStage(stage);
        Mockito.when(stageRepository.getById(task.getStage().getStageId())).thenThrow();

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Create task: check execution")
    public void testCreateTask() {
        Task task = new Task();
        task.setName("Not empty");
        task.setPerformerUserId(1L);
        task.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        task.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(task.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        task.setProject(project);
        Mockito.when(projectRepository.getByIdOrThrow(task.getProject().getId())).thenReturn(project);

        Stage stage = new Stage();
        stage.setStageId(3L);
        task.setStage(stage);
        Mockito.when(stageRepository.getById(task.getStage().getStageId())).thenReturn(stage);

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Update task: check execution")
    public void testUpdateTask() {
        Task task = new Task();
        task.setName("Not empty");
        task.setPerformerUserId(1L);
        task.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        task.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(task.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        task.setProject(project);
        Mockito.when(projectRepository.getByIdOrThrow(task.getProject().getId())).thenReturn(project);

        Stage stage = new Stage();
        stage.setStageId(3L);
        task.setStage(stage);
        Mockito.when(stageRepository.getById(task.getStage().getStageId())).thenReturn(stage);

        assertThrows(RuntimeException.class, () -> taskService.updateTask(task));
    }

    @Test
    @DisplayName("Filters task: check execution")
    public void testGetFilteredTasks() {
        TaskFilterDto filterDto = new TaskFilterDto();
        filterDto.setDescription("test description");
        Task task = new Task();
        task.setDescription("test description");

        UserDto userPerformerDto = new UserDto();
        userPerformerDto.setId(1L);

        Mockito.when(userServiceClient.getUser(1)).thenReturn(userPerformerDto);
        Mockito.when(projectRepository.existsById(3L)).thenReturn(true);
        Mockito.when(taskRepository.findAllByProjectId(3L)).thenReturn(List.of(task));

        Mockito.when(taskFilters.stream()).thenReturn(Stream.of(
                new DescriptionFilter(),
                new PerformerUserFilter()
        ));

        List<Task> tasksByFilter = taskService.getTasks(1L, 3L, filterDto);

        assertThat(task)
                .usingRecursiveComparison()
                .isEqualTo(tasksByFilter.get(0));
    }

    @Test
    @DisplayName("Get all tasks by project id: check execution")
    public void testGetAllTasksByProject() {
        taskRepository.findAllByProjectId(3L);

        Mockito.verify(taskRepository, Mockito.times(1))
                .findAllByProjectId(3L);
    }

    @Test
    @DisplayName("Get task by id: check execution")
    public void testGetTaskById() {
        Task task = new Task();
        task.setId(5L);

        UserDto userPerformerDto = new UserDto();
        userPerformerDto.setId(1L);
        Mockito.when(userServiceClient.getUser(1)).thenReturn(userPerformerDto);

        taskRepository.findById(task.getId());

        Mockito.verify(taskRepository, Mockito.times(1))
                .findById(task.getId());
    }
}
