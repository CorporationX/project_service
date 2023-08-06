package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.Assertions;
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

import static faang.school.projectservice.model.stage.StageStatus.CREATED;
import static faang.school.projectservice.model.stage.StageStatus.IN_PROGRESS;
import static faang.school.projectservice.model.stage.StageStatus.ON_HOLD;
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
    private TaskService taskService;
    @InjectMocks
    private StageService stageService;
    @Spy
    private StageMapperImpl stageMapper;

    private Project project;
    private Stage stage;
    private Stage stageOne;
    private List<Task> tasks;

    @BeforeEach
    void init() {
        project = Project.builder().id(1L).build();

        stage = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .status(CREATED)
                .project(Project.builder().id(1L).status(ProjectStatus.CANCELLED).build())
                .build();

        tasks = new ArrayList<>();
        tasks.add(Task.builder()
                .name("task")
                .status(TaskStatus.TODO)
                .build());

        stageOne = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .tasks(tasks)
                .status(IN_PROGRESS)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .tasks(tasks)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build())
                .build();

        List<Stage> stages = new ArrayList<>();
        stages.add(stage);
        stages.add(stageOne);
        project.setStages(stages);

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
    void testDeleteStage_CancelTasks() {
        List<Task> cancelledTasks = List.of();
        Mockito.when(taskService.cancelTasksOfStage(anyLong())).thenReturn(cancelledTasks);
        Assertions.assertDoesNotThrow(() -> stageService.deleteStage(anyLong()));
    }

    @Test
    void testFindAllStagesOfProject() {
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        stageService.findAllStagesOfProject(anyLong());
        Mockito.verify(projectRepository).getProjectById(anyLong());
    }

    @Test
    void shouldGetStagesWithStatus_emptyList() {
        List<StageDto> expected = new ArrayList<>();
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        final List<StageDto> stagesOfProjectWithFilter = stageService.getStagesOfProjectWithFilter(anyLong(), ON_HOLD);
        Assertions.assertEquals(expected, stagesOfProjectWithFilter);
        Mockito.verify(projectRepository).getProjectById(anyLong());
    }

    @Test
    void shouldGetStagesWithStatus() {
        List<StageDto> expected = List.of(StageDto.builder()
                .stageId(1L)
                .stageName("stage")
                .status(IN_PROGRESS)
                .projectId(1L)
                .build());
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        final List<StageDto> stagesOfProjectWithFilter = stageService.getStagesOfProjectWithFilter(anyLong(), IN_PROGRESS);
        Assertions.assertEquals(expected, stagesOfProjectWithFilter);
        Mockito.verify(projectRepository).getProjectById(anyLong());
    }
}