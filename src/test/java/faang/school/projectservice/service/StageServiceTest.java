package faang.school.projectservice.service;

import faang.school.projectservice.dto.MethodDeletingStageDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilter;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilterImpl;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.mapper.StageRolesMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    @Spy
    private StageMapperImpl stageMapper;

    private Stage stage1;
    private Stage stage2;
    private Stage stage3;

    private Stage stage4;

    private StageDto stageDto1;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);
        stage1 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();
        stage2 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CANCELLED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();
        stage3 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();
        List<Task> tasks = new ArrayList<>();
        Task task = Task.builder()
                .name("task")
                .build();
        tasks.add(task);
        stage4 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
                .stageRoles(List.of())
                .tasks(tasks)
                .build();
        stageDto1 = stageMapper.toDto(stage1);
    }

    @Test
    void testCreateStage() {
        stageService.createStage(stageDto1);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage1);
    }

    @Test
    void testCreateStageNegativeProjectByCancelled() {
        stageDto1.getProject().setStatus(ProjectStatus.CANCELLED);
        assertThrows(DataValidationException.class, () -> stageService.createStage(stageDto1));
    }

    @Test
    void testGetStagesByProjectStatus() {
        Mockito.when(stageRepository.findAll()).thenReturn(List.of(stage1, stage2, stage3));
        List<ProjectStatusFilter> projectStatusFilters = List.of(
                new ProjectStatusFilterImpl()
        );
        ProjectStatusFilterDto filter = ProjectStatusFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();
        stageService = new StageService(stageRepository, taskRepository, stageMapper, projectStatusFilters);

        List<StageDto> actual = stageService.getStagesByProjectStatus(filter);
        List<StageDto> expected = Stream.of(stage1, stage3)
                .map(stageMapper::toDto)
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void deleteStageCascade() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage4);
        stageService.deleteStage(1L, MethodDeletingStageDto.CASCADE, null);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteAll(stage4.getTasks());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage4);
    }

    @Test
    void deleteStageClose() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage4);
        stageService.deleteStage(1L, MethodDeletingStageDto.CLOSE, null);
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage4);
    }

    @Test
    void deleteStageMoveToNextStage() {
        List<Task> tasks = new ArrayList<>();
        Task task = Task.builder()
                .name("task")
                .build();
        tasks.add(task);

        Stage stage = stageMapper.toEntity(
                new StageDto(1L, "stage", ProjectDto.builder().build(), new ArrayList<>()));
        stage.setTasks(tasks);
        Stage stageNew = stageMapper.toEntity(
                new StageDto(2L, "Rtest", ProjectDto.builder().build(), new ArrayList<>()));

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        Mockito.when(stageRepository.getById(2L)).thenReturn(stageNew);
        stageService.deleteStage(1L, MethodDeletingStageDto.MOVE_TO_NEXT_STAGE, 2L);

        stageNew.setTasks(tasks);

        Mockito.verify(stageRepository, Mockito.times(1)).save(stageNew);
        stage.setTasks(new ArrayList<>());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage);
    }
}