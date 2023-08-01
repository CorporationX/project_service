package faang.school.projectservice.service.task;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;
    private Stage stage;
    private Stage stage1;
    private List<Task> tasks;
    private List<Task> tasks1;
    private List<Task> tasks2;

    @BeforeEach
    void init() {
        tasks = new ArrayList<>();
        tasks.add(Task.builder()
                .name("task")
                .status(TaskStatus.TODO)
                .build());

        tasks1 = List.of(Task.builder()
                .name("task")
                .status(TaskStatus.TODO)
                .build());

        tasks2 = new ArrayList<>();

        stage = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .tasks(tasks)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .tasks(tasks)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build())
                .build();

        stage1 = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .tasks(tasks2)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .tasks(tasks2)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build())
                .build();
    }

    @Test
    public void testGetAllTasksOfStage() {
        Mockito.when(stageRepository.getById(stage.getStageId())).thenReturn(stage);
        Assertions.assertEquals(tasks.size(), taskService.getAllTasksOfStage(stage.getStageId()).size());
    }

    @Test
    public void testCancelingTasksOfStage() {
        Mockito.when(stageRepository.getById(stage.getStageId())).thenReturn(stage);
        Assertions.assertNotEquals(tasks1, taskService.cancelTasksOfStage(stage.getStageId()));
    }

    @Test
    public void testCancelingTasksOfStage_isEmpty() {
        Mockito.when(stageRepository.getById(stage1.getStageId())).thenReturn(stage1);
        Assertions.assertThrows(DataValidationException.class, () -> taskService.cancelTasksOfStage(stage1.getStageId()));
    }
}
