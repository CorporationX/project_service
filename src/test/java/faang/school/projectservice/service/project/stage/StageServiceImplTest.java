package faang.school.projectservice.service.project.stage;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.StageRoleDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.project.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.stage.filters.StageFilter;
import faang.school.projectservice.service.project.stage.remove.RemoveStrategyExecutor;
import faang.school.projectservice.service.project.stage.remove.RemoveWithClosingTasks;
import faang.school.projectservice.service.project.stage.remove.RemoveWithTasks;
import faang.school.projectservice.service.project.stage.remove.RemoveWithTasksMigration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceImplTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long STAGE_ID = 1L;
    private static final Long USER_ID = 1L;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private StageMapper stageMapper = Mappers.getMapper(StageMapper.class);

    private List<StageFilter> stageFilters;

    private StageServiceImpl stageService;

    private RemoveTypeDto removeTypeDto;
    private final StageFilterDto stageFilterDto = StageFilterDto.builder().build();
    private StageCreateDto stageCreateDto;
    private Project project;

    @BeforeEach
    void setUp() {
        stageFilters = List.of(
                Mockito.mock(StageFilter.class),
                Mockito.mock(StageFilter.class));
        List<RemoveStrategyExecutor> removeStrategies = List.of(
                new RemoveWithClosingTasks(stageRepository, taskRepository, RemoveStrategy.CLOSE),
                new RemoveWithTasks(stageRepository, RemoveStrategy.CASCADE_DELETE),
                new RemoveWithTasksMigration(stageRepository, RemoveStrategy.MIGRATE));
        stageService = new StageServiceImpl(
                stageRepository,
                projectRepository,
                teamMemberRepository,
                stageMapper,
                stageFilters,
                removeStrategies);
        stageService.fillRemoveStrategyExecutors();
        project = Project.builder()
                .id(PROJECT_ID)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.DEVELOPER)
                                .count(3)
                                .build(),
                        StageRoleDto.builder()
                                .teamRole(TeamRole.DESIGNER)
                                .count(2)
                                .build()))
                .build();
    }

    @Test
    @DisplayName("Creating stage")
    void stageServiceTest_createStage() {
        Stage savedStage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .build();
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(stageRepository.save(any(Stage.class))).thenReturn(savedStage);

        StageDto result = stageService.createStage(stageCreateDto);

        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(stageRepository).save(any(Stage.class));
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Creating stage for non existing project")
    void stageServiceTest_createStageForNonExistingProject() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> stageService.createStage(stageCreateDto));
        verify(projectRepository).existsById(PROJECT_ID);
    }

    @Test
    @DisplayName("Creating stage for project with COMPLETED status")
    void stageServiceTest_createStageForProjectWithCompletedStatus() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.createStage(stageCreateDto));
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
    }

    @Test
    @DisplayName("Creating stage for project with CANCELLED status")
    void stageServiceTest_createStageForProjectWithCancelledStatus() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.createStage(stageCreateDto));
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
    }

    @Test
    @DisplayName("Getting stages from project with filters")
    void stageServiceTest_getStagesFromProjectWithFilters() {
        List<Stage> stages = initStages();
        project.setStages(stages);
        List<StageDto> expectedDtos = List.of(
                StageDto.builder()
                        .stageId(1L)
                        .stageName("test")
                        .projectId(PROJECT_ID)
                        .build(),
                StageDto.builder()
                        .stageId(2L)
                        .stageName("test2")
                        .projectId(PROJECT_ID)
                        .build());
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(stageFilters.get(0).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(1).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(0).apply(any(), any())).thenReturn(stages.stream());
        when(stageFilters.get(1).apply(any(), any())).thenReturn(stages.stream());

        var result = stageService.getStages(PROJECT_ID, stageFilterDto);

        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(stageFilters.get(0)).isApplicable(any());
        verify(stageFilters.get(1)).isApplicable(any());
        verify(stageFilters.get(0)).apply(any(), any());
        verify(stageFilters.get(1)).apply(any(), any());
        assertEquals(expectedDtos, result);
    }

    @Test
    @DisplayName("Getting stages from project with one filter")
    void stageServiceTest_getStagesFromProjectWithOneFilter() {
        List<Stage> stages = initStages();
        project.setStages(stages);
        List<StageDto> expectedDtos = List.of(
                StageDto.builder()
                        .stageId(1L)
                        .stageName("test")
                        .projectId(PROJECT_ID)
                        .build(),
                StageDto.builder()
                        .stageId(2L)
                        .stageName("test2")
                        .projectId(PROJECT_ID)
                        .build());
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(stageFilters.get(0).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(1).isApplicable(any())).thenReturn(false);
        when(stageFilters.get(0).apply(any(), any())).thenReturn(stages.stream());

        var result = stageService.getStages(PROJECT_ID, stageFilterDto);

        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(stageFilters.get(0)).isApplicable(any());
        verify(stageFilters.get(1)).isApplicable(any());
        verify(stageFilters.get(0)).apply(any(), any());
        verify(stageFilters.get(1), never()).apply(any(), any());
        assertEquals(expectedDtos, result);
    }

    @Test
    @DisplayName("Getting stages from project without filters")
    void stageServiceTest_getStagesFromProjectWithoutFilters() {
        List<Stage> stages = initStages();
        project.setStages(stages);
        List<StageDto> expectedDtos = List.of(
                StageDto.builder()
                        .stageId(1L)
                        .stageName("test")
                        .projectId(PROJECT_ID)
                        .build(),
                StageDto.builder()
                        .stageId(2L)
                        .stageName("test2")
                        .projectId(PROJECT_ID)
                        .build());
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(stageFilters.get(0).isApplicable(any())).thenReturn(false);
        when(stageFilters.get(1).isApplicable(any())).thenReturn(false);

        var result = stageService.getStages(PROJECT_ID, stageFilterDto);

        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(stageFilters.get(0)).isApplicable(any());
        verify(stageFilters.get(1)).isApplicable(any());
        verify(stageFilters.get(0), never()).apply(any(), any());
        verify(stageFilters.get(1), never()).apply(any(), any());
        assertEquals(expectedDtos, result);
    }

    @Test
    @DisplayName("Getting empty list of stages from project with filters")
    void stageServiceTest_getEmptyStagesFromProjectWithFilters() {
        List<Stage> stages = new ArrayList<>();
        project.setStages(stages);
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(stageFilters.get(0).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(1).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(0).apply(any(), any())).thenReturn(stages.stream());
        when(stageFilters.get(1).apply(any(), any())).thenReturn(stages.stream());

        var result = stageService.getStages(PROJECT_ID, stageFilterDto);

        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(stageFilters.get(0)).isApplicable(any());
        verify(stageFilters.get(1)).isApplicable(any());
        verify(stageFilters.get(0)).apply(any(), any());
        verify(stageFilters.get(1)).apply(any(), any());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Getting stages from non existing project")
    void stageServiceTest_getStagesFromNonExistingProject() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> stageService.getStages(PROJECT_ID, stageFilterDto));
        verify(projectRepository).existsById(PROJECT_ID);
    }

    @Test
    @DisplayName("Try getting non existing stages from project")
    void stageServiceTest_getNonExistingStagesFromProject() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);

        assertThrows(EntityNotFoundException.class, () -> stageService.getStages(PROJECT_ID, stageFilterDto));
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
    }

    @Test
    @DisplayName("Remove stage with cascade delete tasks")
    void stageServiceTest_removeStageWithCascadeDeleteTasks() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.CASCADE_DELETE)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);

        StageDto result = stageService.removeStage(STAGE_ID, removeTypeDto);

        verify(stageRepository).getById(STAGE_ID);
        verify(stageRepository).delete(stage);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Remove stage with closing task")
    void stageServiceTest_removeStageWithClosingTask() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.CLOSE)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);

        StageDto result = stageService.removeStage(STAGE_ID, removeTypeDto);

        verify(stageRepository).getById(STAGE_ID);
        verify(stageRepository).delete(stage);
        assertEquals(expectedDto, result);
        assertTrue(tasks.stream()
                .map(Task::getStatus)
                .allMatch(status -> status.equals(TaskStatus.CANCELLED)));
    }

    @Test
    @DisplayName("Remove stage with migrate task to stage without tasks")
    void stageServiceTest_removeStageWithMigrateTaskToStageWithoutTasks() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.MIGRATE)
                .stageForMigrateId(2L)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        Stage stageToMigrate = Stage.builder()
                .stageId(2L)
                .stageName("test2")
                .project(project)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stageToMigrate);
        when(stageRepository.save(stageToMigrate)).thenReturn(stageToMigrate);

        StageDto result = stageService.removeStage(STAGE_ID, removeTypeDto);

        verify(stageRepository, times(2)).getById(any());
        verify(stageRepository).delete(stage);
        assertEquals(tasks, stageToMigrate.getTasks());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Remove stage with migrate task to stage with tasks")
    void stageServiceTest_removeStageWithMigrateTaskToStageWithTasks() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.MIGRATE)
                .stageForMigrateId(2L)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        Stage stageToMigrate = Stage.builder()
                .stageId(2L)
                .stageName("test2")
                .project(project)
                .build();
        stageToMigrate.setTasks(new ArrayList<>(List.of(
                Task.builder()
                        .id(3L)
                        .name("test3")
                        .stage(stageToMigrate)
                        .build())));
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        var expectedTasks = new ArrayList<>(tasks);
        expectedTasks.addAll(stageToMigrate.getTasks());
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stageToMigrate);
        when(stageRepository.save(stageToMigrate)).thenReturn(stageToMigrate);

        StageDto result = stageService.removeStage(STAGE_ID, removeTypeDto);

        verify(stageRepository, times(2)).getById(any());
        verify(stageRepository).delete(stage);
        assertTrue(expectedTasks.containsAll(stageToMigrate.getTasks()));
        assertTrue(stageToMigrate.getTasks().containsAll(expectedTasks));
        //не спрашивайте, поймал какой то глубинный баг, assertEquals, assertIterableEquals на списках выдает StackOverflowError
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Remove stage with closing non existing tasks")
    void stageServiceTest_removeStageWithClosingNonExistingTasks() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.CLOSE)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);

        assertThrows(EntityNotFoundException.class, () -> stageService.removeStage(STAGE_ID, removeTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("Remove stage with migrate non existing tasks")
    void stageServiceTest_removeStageWithMigrateNonExistingTasks() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.MIGRATE)
                .stageForMigrateId(2L)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);

        assertThrows(EntityNotFoundException.class, () -> stageService.removeStage(STAGE_ID, removeTypeDto));
        verify(stageRepository).getById(any());
    }

    @Test
    @DisplayName("Remove stage with migrate task without stage in dto")
    void stageServiceTest_removeStageWithMigrateTaskWithoutStageInRemoveTypeDto() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.MIGRATE)
                .stageForMigrateId(null)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);

        assertThrows(DataValidationException.class, () -> stageService.removeStage(STAGE_ID, removeTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("Remove stage with migrate task with non existing stage to migrate")
    void stageServiceTest_removeStageWithMigrateTaskWithNonExistingStageFroMigrate() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.MIGRATE)
                .stageForMigrateId(2L)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageService.removeStage(STAGE_ID, removeTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("Remove non existing stage with migrate task")
    void stageServiceTest_removeStageWithMigrateTaskWithNonExistingStage() {
        removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.MIGRATE)
                .stageForMigrateId(2L)
                .build();
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageService.removeStage(STAGE_ID, removeTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("Updating stage name and executors")
    void stageServiceTest_updateStageNameAndExecutors() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .stageName("new name")
                .executorIds(executorIds)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("new name")
                .projectId(PROJECT_ID)
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID);

        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(stageUpdateDto.stageName(), result.stageName());
        assertEquals(executorIds, result.executorIds());
    }

    @Test
    @DisplayName("Updating stage executors")
    void stageServiceTest_updateExecutors() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID);

        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.executorIds());
    }

    @Test
    @DisplayName("Updating stage executors with already having stages")
    void stageServiceTest_updateExecutorsWithAlreadyHavingStages() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        Stage antoherExecutorsStage = Stage.builder()
                .stageId(2L)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        executors.forEach(executor -> executor.setStages(new ArrayList<>(List.of(antoherExecutorsStage))));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID);

        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.executorIds());
        assertEquals(List.of(antoherExecutorsStage, stage), executors.get(0).getStages());
        assertEquals(List.of(antoherExecutorsStage, stage), executors.get(1).getStages());
    }

    @Test
    @DisplayName("Updating stage executors with empty list of stages")
    void stageServiceTest_updateExecutorsWithEmptyStages() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        executors.forEach(executor -> executor.setStages(new ArrayList<>()));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID);

        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.executorIds());
        assertEquals(List.of(stage), executors.get(0).getStages());
        assertEquals(List.of(stage), executors.get(1).getStages());
    }

    @Test
    @DisplayName("Updating stage executors with not enough executors for roles")
    void stageServiceTest_updateExecutorsWithNotEnoughExecutorsForRoles() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        List<Team> teams = initTeams();
        project.setTeams(teams);
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID);

        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.executorIds());
    }

    @Test
    @DisplayName("Updating stage executors with not enough executors for roles and executors in project teams")
    void stageServiceTest_updateExecutorsWithNotEnoughExecutorsForRolesAndExecutorsInProjectTeams() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        List<Team> teams = initTeams();
        project.setTeams(teams);
        List<TeamMember> executors = initExecutors();
        project.getTeams().add(Team.builder()
                .id(3L)
                .teamMembers(executors)
                .project(project)
                .build());
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID);

        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.executorIds());
    }

    @Test
    @DisplayName("Updating with executors whithout needed roles")
    void stageServiceTest_updateWithExecutorsWithoutNeededRoles() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        executors.get(0).setRoles(List.of(TeamRole.OWNER));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);

        assertThrows(DataValidationException.class, () -> stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID));
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
    }

    @Test
    @DisplayName("Updating stage with not enough executors and project with non existing teams")
    void stageServiceTest_updateStageWithNotEnoughExecutorsAndProjectWithNonExistingTeams() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);

        assertThrows(EntityNotFoundException.class, () -> stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID));
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
    }

    @Test
    @DisplayName("Updating stage with not enough executors in project")
    void stageServiceTest_updateStageWithNotEnoughExecutorsInProject() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        project.getTeams().add(Team.builder()
                .id(3L)
                .teamMembers(executors)
                .project(project)
                .build());
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(executorIds)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);

        assertThrows(EntityNotFoundException.class, () -> stageService.updateStage(stageUpdateDto, STAGE_ID, USER_ID));
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
    }

    @Test
    @DisplayName("Get stage")
    void stageServiceTest_getStage() {
        Stage stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .project(project)
                .build();
        StageDto expectedDto = StageDto.builder()
                .stageId(STAGE_ID)
                .stageName("test")
                .projectId(PROJECT_ID)
                .build();
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);

        StageDto result = stageService.getStage(STAGE_ID);

        verify(stageRepository).getById(STAGE_ID);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Get non existing stage")
    void stageServiceTest_getNonExistingStage() {
        when(stageRepository.getById(STAGE_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageService.getStage(STAGE_ID));
        verify(stageRepository).getById(STAGE_ID);
    }

    private List<Stage> initStages() {
        return List.of(
                Stage.builder()
                        .stageId(1L)
                        .stageName("test")
                        .project(project)
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .stageName("test2")
                        .project(project)
                        .build());
    }

    private List<Task> initTasks(Stage stage) {
        return List.of(
                Task.builder()
                        .id(1L)
                        .name("test")
                        .stage(stage)
                        .build(),
                Task.builder()
                        .id(2L)
                        .name("test2")
                        .stage(stage)
                        .build());
    }

    private List<StageRoles> initStageRoles(Stage stage) {
        return List.of(
                StageRoles.builder()
                        .id(1L)
                        .teamRole(TeamRole.DEVELOPER)
                        .count(1)
                        .stage(stage)
                        .build(),
                StageRoles.builder()
                        .id(2L)
                        .teamRole(TeamRole.DESIGNER)
                        .count(1)
                        .stage(stage)
                        .build());
    }

    private List<TeamMember> initExecutors() {
        return List.of(
                TeamMember.builder()
                        .id(1L)
                        .userId(2L)
                        .roles(List.of(
                                TeamRole.DEVELOPER,
                                TeamRole.MANAGER
                        ))
                        .build(),
                TeamMember.builder()
                        .id(2L)
                        .userId(2L)
                        .roles(List.of(
                                TeamRole.DESIGNER,
                                TeamRole.OWNER
                        ))
                        .build());
    }

    private List<Team> initTeams() {
        List<TeamMember> firstTeamMembers = List.of(TeamMember.builder()
                .id(3L)
                .userId(3L)
                .roles(List.of(
                        TeamRole.DESIGNER,
                        TeamRole.INTERN))
                .build());
        List<TeamMember> secondTeamMembers = List.of(TeamMember.builder()
                .id(4L)
                .userId(4L)
                .roles(List.of(
                        TeamRole.DEVELOPER,
                        TeamRole.INTERN))
                .build());
        return new ArrayList<>(List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(firstTeamMembers)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(1L)
                        .teamMembers(secondTeamMembers)
                        .project(project)
                        .build()));
    }
}
