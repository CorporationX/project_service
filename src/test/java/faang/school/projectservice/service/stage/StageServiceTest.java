package faang.school.projectservice.service.stage;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private StageService stageService;
    @Spy
    private StageMapperImpl stageMapper;

    private Stage stage;
    private Stage stage1;
    private List<Task> tasks;

    @BeforeEach
    void init() {
        stage = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CANCELLED).build())
                .build();

        tasks = new ArrayList<>();
        tasks.add(Task.builder()
                .name("task")
                .status(TaskStatus.TODO)
                .build());

        stage1 = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .tasks(tasks)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .tasks(tasks)
                        .status(ProjectStatus.TODO)
                        .build())
                .build();
    }

    @Test
    void testCreateStageNegativeProjectByCancelled() {
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        assertThrows(DataValidationException.class, () -> stageService.createStage(stageMapper.toDto(stage)));
        Mockito.verify(stageRepository, Mockito.times(0)).save(stage);
    }

    @Test
    void testCreateStageNegativeProjectByCompleted() {
        stage.setProject(Project.builder().id(1L).name("project").status(ProjectStatus.COMPLETED).build());
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());

        assertThrows(DataValidationException.class, () -> stageService.createStage(stageMapper.toDto(stage)));
        Mockito.verify(stageRepository, Mockito.times(0)).save(stage);
    }

    @Test
    void testCreateStagePositive() {
        stage.setProject(Project.builder().id(1L).name("project").status(ProjectStatus.CREATED).build());
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        Mockito.when(stageRepository.save(any())).thenReturn(stage);

        stageService.createStage(stageMapper.toDto(stage));

        Mockito.verify(stageMapper, Mockito.times(2)).toDto(stage);
    }

    @Test
    void testDeleteStage_ChangeStatus() {
        Mockito.when(stageRepository.getById(anyLong())).thenReturn(stage1);
        stageService.deleteStage(anyLong());
        Mockito.verify(taskRepository).saveAll(tasks);
        Mockito.verify(stageRepository).delete(stage1);
    }
}