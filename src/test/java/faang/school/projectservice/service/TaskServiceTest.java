package faang.school.projectservice.service;

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

import org.junit.Assert;
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

    @Test
    @DisplayName("Create task: check name is blank")
    public void testCreateTaskNameIsBlank() {
        Task t = new Task();
        t.setName("");

        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

    @Test
    @DisplayName("Create task: check user performer exist")
    public void testCreateTaskCheckUserPerformerExists() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(25L);

        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

    @Test
    @DisplayName("Create task: check user reporter exist")
    public void testCreateTaskCheckUserReporterExists() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(1L);
        t.setReporterUserId(25L);

        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

//    @Test
//    public void testCreateTaskCheckProjectExists() {
//        Task t = new Task();
//        t.setName("Not empty");
//        t.setPerformerUserId(1L);
//        t.setReporterUserId(1L);
//
//        Project p = new Project();
//        p.setId(1L);
//        t.setProject(p);
//        Mockito.when(projectRepository.existsById(t.getProject().getId())).thenReturn(false);
//
//        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
//    }

    @Test
    @DisplayName("Create task: check parent task")
    public void testCreateTaskCheckParentTask() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(1L);
        t.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        t.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(t.getParentTask().getId())).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

    @Test
    @DisplayName("Create task: check project")
    public void testCreateTaskCheckProject() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(1L);
        t.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        t.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(t.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        t.setProject(project);
        Mockito.when(projectRepository.getProjectById(t.getProject().getId())).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

    @Test
    @DisplayName("Create task: check stage")
    public void testCreateTaskCheckStage() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(1L);
        t.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        t.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(t.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        t.setProject(project);
        Mockito.when(projectRepository.getProjectById(t.getProject().getId())).thenReturn(project);

        Stage stage = new Stage();
        stage.setStageId(3L);
        t.setStage(stage);
        Mockito.when(stageRepository.getById(t.getStage().getStageId())).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

    @Test
    @DisplayName("Create task: check execution")
    public void testCreateTask() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(1L);
        t.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        t.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(t.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        t.setProject(project);
        Mockito.when(projectRepository.getProjectById(t.getProject().getId())).thenReturn(project);

        Stage stage = new Stage();
        stage.setStageId(3L);
        t.setStage(stage);
        Mockito.when(stageRepository.getById(t.getStage().getStageId())).thenReturn(stage);


        Assert.assertThrows(RuntimeException.class, () -> taskService.createTask(t));
    }

    @Test
    @DisplayName("Update task: check execution")
    public void testUpdateTask() {
        Task t = new Task();
        t.setName("Not empty");
        t.setPerformerUserId(1L);
        t.setReporterUserId(1L);

        Task parentTask = new Task();
        parentTask.setId(2L);
        t.setParentTask(parentTask);
        Mockito.when(taskRepository.findById(t.getParentTask().getId())).thenReturn(Optional.of(parentTask));

        Project project = new Project();
        project.setId(3L);
        t.setProject(project);
        Mockito.when(projectRepository.getProjectById(t.getProject().getId())).thenReturn(project);

        Stage stage = new Stage();
        stage.setStageId(3L);
        t.setStage(stage);
        Mockito.when(stageRepository.getById(t.getStage().getStageId())).thenReturn(stage);

        Assert.assertThrows(RuntimeException.class, () -> taskService.updateTask(t));
    }

    @Test
    @DisplayName("Filters task: check execution")
    public void testGetFilteredTasks() {
        TaskFilterDto filterDto = new TaskFilterDto();
        filterDto.setDescription("test description");
        Task task = new Task();
        task.setDescription("test description");
        Mockito.when(taskRepository.findAll()).thenReturn(List.of(task));

        Mockito.when(taskFilters.stream()).thenReturn(Stream.of(
                new DescriptionFilter(),
                new PerformerUserFilter()
        ));

        List<Task> tasksByFilter = taskService.getFilteredTasks(1L, filterDto);

        assertThat(task)
                .usingRecursiveAssertion()
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
        Task t = new Task();
        t.setId(5L);

        taskRepository.findById(t.getId());

        Mockito.verify(taskRepository, Mockito.times(1))
                .findById(t.getId());
    }
}
