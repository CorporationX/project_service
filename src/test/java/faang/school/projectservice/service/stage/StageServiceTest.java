package faang.school.projectservice.service.stage;

import faang.school.projectservice.deletestrategy.DeleteStrategyRegistry;
import faang.school.projectservice.deletestrategy.DeleteWithClosingTasks;
import faang.school.projectservice.deletestrategy.DeleteWithTasksMigration;
import faang.school.projectservice.dto.stage.DeleteTypeDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRoleDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.entity.stage.DeleteStrategy;
import faang.school.projectservice.exception.ExecutorRoleNotValidException;
import faang.school.projectservice.exception.NotEnoughTeamMembersException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.StageIdRequiredException;
import faang.school.projectservice.exception.WrongProjectStatusException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
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
import faang.school.projectservice.filters.StageFilter;
import faang.school.projectservice.deletestrategy.DeleteWithTasks;
import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long STAGE_ID = 1L;

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

    private StageService stageService;

    private DeleteTypeDto deleteTypeDto;
    private final StageFilterDto stageFilterDto = StageFilterDto.builder().build();
    private StageCreateDto stageCreateDto;
    private Project project;

    @BeforeEach
    void setUp() {
        stageFilters = List.of(
                Mockito.mock(StageFilter.class),
                Mockito.mock(StageFilter.class));

        DeleteStrategyRegistry registry = new DeleteStrategyRegistry(List.of(
                new DeleteWithClosingTasks(stageRepository, taskRepository),
                new DeleteWithTasks(stageRepository),
                new DeleteWithTasksMigration(stageRepository)
        ));

        stageService = new StageService(
                stageRepository,
                projectRepository,
                teamMemberRepository,
                stageMapper,
                stageFilters,
                registry);

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
    @DisplayName("Create stage - Success scenario")
    void createStage_Success() {
        Stage expectedStage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, new ArrayList<>());
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(stageRepository.save(any(Stage.class))).thenReturn(expectedStage);
        StageDto result = stageService.createStage(stageCreateDto);
        assertEquals(expectedDto, result);
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
        verify(stageRepository).save(any(Stage.class));
    }

    @Test
    @DisplayName("Create stage - Project not found")
    void createStage_ProjectNotFound() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
        assertThrows(ProjectNotFoundException.class,
                () -> stageService.createStage(stageCreateDto));
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository, never()).getProjectById(any());
        verify(stageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Creating stage for project with COMPLETED status")
    void stageServiceTest_createStageForProjectWithCompletedStatus() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        assertThrows(WrongProjectStatusException.class, () -> stageService.createStage(stageCreateDto));
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
    }

    @Test
    @DisplayName("Creating stage for project with CANCELLED status")
    void stageServiceTest_createStageForProjectWithCancelledStatus() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        assertThrows(WrongProjectStatusException.class, () -> stageService.createStage(stageCreateDto));
        verify(projectRepository).existsById(PROJECT_ID);
        verify(projectRepository).getProjectById(PROJECT_ID);
    }

    @Test
    @DisplayName("Getting stages from project with filters")
    void stageServiceTest_getStagesFromProjectWithFilters() {
        List<Stage> stages = initStages();
        project.setStages(stages);
        List<StageDto> expectedDtos = List.of(
                initStageDto(1L, "test", PROJECT_ID, new ArrayList<>()),
                initStageDto(2L, "test2", PROJECT_ID, new ArrayList<>()));
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
                initStageDto(1L, "test", PROJECT_ID, new ArrayList<>()),
                initStageDto(2L, "test2", PROJECT_ID, new ArrayList<>()));
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
                initStageDto(1L, "test", PROJECT_ID, new ArrayList<>()),
                initStageDto(2L, "test2", PROJECT_ID, new ArrayList<>()));
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
        assertThrows(ProjectNotFoundException.class, () -> stageService.getStages(PROJECT_ID, stageFilterDto));
        verify(projectRepository).existsById(PROJECT_ID);
    }

    @Test
    @DisplayName("Delete stage - Success with cascade delete")
    void deleteStage_SuccessWithCascadeDelete() {
        DeleteTypeDto deleteTypeDto = initDeleteTypeDto(DeleteStrategy.CASCADE_DELETE, null);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        StageDto result = stageService.deleteStage(STAGE_ID, deleteTypeDto);
        assertNotNull(result);
        verify(stageRepository).delete(stage);
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("delete stage with closing task")
    void stageServiceTest_removeStageWithClosingTask() {
        deleteTypeDto = initDeleteTypeDto(DeleteStrategy.CLOSE_TASKS, null);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        StageDto result = stageService.deleteStage(STAGE_ID, deleteTypeDto);
        verify(stageRepository).getById(STAGE_ID);
        verify(stageRepository).delete(stage);
        assertEquals(expectedDto, result);
        assertTrue(tasks.stream()
                .map(Task::getStatus)
                .allMatch(status -> status.equals(TaskStatus.CANCELLED)));
    }

    @Test
    @DisplayName("Delete stage with migrate task to stage without tasks")
    void stageServiceTest_deleteStageWithMigrateTaskToStageWithoutTasks() {
        deleteTypeDto = initDeleteTypeDto(DeleteStrategy.MOVE_TASKS, 2L);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        Stage stageToMigrate = initStage(2L, "test2", project, new ArrayList<>());
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stageToMigrate);
        when(stageRepository.save(stageToMigrate)).thenReturn(stageToMigrate);
        StageDto result = stageService.deleteStage(STAGE_ID, deleteTypeDto);
        verify(stageRepository, times(2)).getById(any());
        verify(stageRepository).delete(stage);
        assertEquals(tasks, stageToMigrate.getTasks());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("delete stage with migrate task to stage with tasks")
    void stageServiceTest_deleteStageWithMigrateTaskToStageWithTasks() {
        deleteTypeDto = initDeleteTypeDto(DeleteStrategy.MOVE_TASKS, 2L);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        Stage stageToMigrate = initStage(2L, "test2", project, new ArrayList<>());
        stageToMigrate.setTasks(new ArrayList<>(List.of(
                initTask(3L, "test3", stageToMigrate))));
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        var expectedTasks = new ArrayList<>(tasks);
        expectedTasks.addAll(stageToMigrate.getTasks());
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stageToMigrate);
        when(stageRepository.save(stageToMigrate)).thenReturn(stageToMigrate);
        StageDto result = stageService.deleteStage(STAGE_ID, deleteTypeDto);
        verify(stageRepository, times(2)).getById(any());
        verify(stageRepository).delete(stage);
        assertTrue(expectedTasks.containsAll(stageToMigrate.getTasks()));
        assertTrue(stageToMigrate.getTasks().containsAll(expectedTasks));
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("delete stage with migrate task without stage in dto")
    void stageServiceTest_deleteStageWithMigrateTaskWithoutStageIndeleteTypeDto() {
        deleteTypeDto = initDeleteTypeDto(DeleteStrategy.MOVE_TASKS, null);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        assertThrows(StageIdRequiredException.class, () -> stageService.deleteStage(STAGE_ID, deleteTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("delete stage with migrate task with non existing stage to migrate")
    void stageServiceTest_deleteStageWithMigrateTaskWithNonExistingStageFroMigrate() {
        deleteTypeDto = initDeleteTypeDto(DeleteStrategy.MOVE_TASKS, 2L);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> stageService.deleteStage(STAGE_ID, deleteTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("delete non existing stage with migrate task")
    void stageServiceTest_deleteStageWithMigrateTaskWithNonExistingStage() {
        deleteTypeDto = initDeleteTypeDto(DeleteStrategy.MOVE_TASKS, 2L);
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<Task> tasks = initTasks(stage);
        stage.setTasks(tasks);
        when(stageRepository.getById(STAGE_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> stageService.deleteStage(STAGE_ID, deleteTypeDto));
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("Updating stage name and executors")
    void stageServiceTest_updateStageNameAndExecutors() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto("new name", executorIds);
        StageDto expectedDto = initStageDto(STAGE_ID, "new name", PROJECT_ID, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);
        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID);
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(stageUpdateDto.stageName(), result.getStageName());
        assertEquals(executorIds, result.getExecutorIds());
    }

    @Test
    @DisplayName("Updating stage executors")
    void stageServiceTest_updateExecutors() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);
        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID);
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.getExecutorIds());
    }

    @Test
    @DisplayName("Updating stage executors with already having stages")
    void stageServiceTest_updateExecutorsWithAlreadyHavingStages() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        Stage antoherExecutorsStage = initStage(2L, "test2", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        executors.forEach(executor -> executor.setStages(new ArrayList<>(List.of(antoherExecutorsStage))));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);
        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID);
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.getExecutorIds());
        assertEquals(List.of(antoherExecutorsStage, stage), executors.get(0).getStages());
        assertEquals(List.of(antoherExecutorsStage, stage), executors.get(1).getStages());
    }

    @Test
    @DisplayName("Updating stage executors with empty list of stages")
    void stageServiceTest_updateExecutorsWithEmptyStages() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        executors.forEach(executor -> executor.setStages(new ArrayList<>()));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);
        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID);
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.getExecutorIds());
        assertEquals(List.of(stage), executors.get(0).getStages());
        assertEquals(List.of(stage), executors.get(1).getStages());
    }

    @Test
    @DisplayName("Updating stage executors with not enough executors for roles")
    void stageServiceTest_updateExecutorsWithNotEnoughExecutorsForRoles() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        List<Team> teams = initTeams();
        project.setTeams(teams);
        List<TeamMember> executors = initExecutors();
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);
        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID);
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.getExecutorIds());
    }

    @Test
    @DisplayName("Updating stage executors with not enough executors for roles and executors in project teams")
    void stageServiceTest_updateExecutorsWithNotEnoughExecutorsForRolesAndExecutorsInProjectTeams() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        List<Team> teams = initTeams();
        project.setTeams(teams);
        List<TeamMember> executors = initExecutors();
        project.getTeams().add(initTeam(3L, executors, project));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        when(stageRepository.save(stage)).thenReturn(stage);
        StageDto result = stageService.updateStage(stageUpdateDto, STAGE_ID);
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
        verify(stageRepository).save(stage);
        assertEquals(expectedDto, result);
        assertEquals(executorIds, result.getExecutorIds());
    }

    @Test
    @DisplayName("Updating with executors without needed roles")
    void stageServiceTest_updateWithExecutorsWithoutNeededRoles() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        executors.get(0).setRoles(List.of(TeamRole.OWNER));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        assertThrows(ExecutorRoleNotValidException.class, () -> stageService.updateStage(stageUpdateDto, STAGE_ID));
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
    }

    @Test
    @DisplayName("Updating stage with not enough executors in project")
    void stageServiceTest_updateStageWithNotEnoughExecutorsInProject() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        List<StageRoles> roles = initStageRoles(stage);
        roles.get(0).setCount(2);
        roles.get(1).setCount(2);
        stage.setStageRoles(roles);
        project.setTeams(new ArrayList<>());
        List<TeamMember> executors = initExecutors();
        project.getTeams().add(initTeam(3L, executors, project));
        List<Long> executorIds = executors.stream().map(TeamMember::getId).toList();
        StageUpdateDto stageUpdateDto = initStageUpdateDto(null, executorIds);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(teamMemberRepository.findAllByIds(executorIds)).thenReturn(executors);
        assertThrows(NotEnoughTeamMembersException.class, () -> stageService.updateStage(stageUpdateDto, STAGE_ID));
        verify(stageRepository).getById(STAGE_ID);
        verify(teamMemberRepository).findAllByIds(executorIds);
    }

    @Test
    @DisplayName("Get stage - Success scenario")
    void getStage_Success() {
        Stage stage = initStage(STAGE_ID, "test", project, new ArrayList<>());
        StageDto expectedDto = initStageDto(STAGE_ID, "test", PROJECT_ID, new ArrayList<>());
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        StageDto result = stageService.getStage(STAGE_ID);
        assertEquals(expectedDto, result);
        verify(stageRepository).getById(STAGE_ID);
    }

    @Test
    @DisplayName("Get non existing stage")
    void stageServiceTest_getNonExistingStage() {
        when(stageRepository.getById(STAGE_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> stageService.getStage(STAGE_ID));
        verify(stageRepository).getById(STAGE_ID);
    }

    private Stage initStage(Long stageId, String stageName, Project project, List<TeamMember> executors) {
        return Stage.builder()
                .stageId(stageId)
                .stageName(stageName)
                .project(project)
                .tasks(new ArrayList<>())
                .executors(executors)
                .build();
    }

    private StageDto initStageDto(Long stageId, String stageName, Long projectId, List<Long> executors) {
        return StageDto.builder()
                .stageId(stageId)
                .stageName(stageName)
                .projectId(projectId)
                .executorIds(executors)
                .build();
    }

    private StageUpdateDto initStageUpdateDto(String newName, List<Long> executorIds) {
        return StageUpdateDto.builder()
                .stageName(newName)
                .executorIds(executorIds)
                .build();
    }

    private DeleteTypeDto initDeleteTypeDto(DeleteStrategy deleteStrategy, Long stageForMigrateId) {
        return DeleteTypeDto.builder()
                .deleteStrategy(deleteStrategy)
                .stageForMigrateId(stageForMigrateId)
                .build();
    }

    private List<Task> initTasks(Stage stage) {
        return List.of(
                initTask(1L, "test", stage),
                initTask(2L, "test2", stage));
    }

    private Task initTask(Long id, String name, Stage stage) {
        return Task.builder()
                .id(id)
                .name(name)
                .stage(stage)
                .build();
    }

    private List<Stage> initStages() {
        return List.of(
                initStage(1L, "test", project, new ArrayList<>()),
                initStage(2L, "test2", project, new ArrayList<>()));
    }

    private StageRoles initStageRole(Long id, TeamRole teamRole, int count, Stage stage) {
        return StageRoles.builder()
                .id(id)
                .teamRole(teamRole)
                .count(count)
                .stage(stage)
                .build();
    }

    private TeamMember initTeamMember(Long id, Long userId, List<TeamRole> teamRole, List<Stage> stages) {
        return TeamMember.builder()
                .id(id)
                .userId(userId)
                .roles(teamRole)
                .stages(stages)
                .build();
    }

    private Team initTeam(Long id, List<TeamMember> members, Project project) {
        return Team.builder()
                .id(id)
                .teamMembers(members)
                .project(project)
                .build();
    }

    private List<StageRoles> initStageRoles(Stage stage) {
        return List.of(
                initStageRole(1L, TeamRole.DEVELOPER, 1, stage),
                initStageRole(2L, TeamRole.DESIGNER, 1, stage));
    }

    private List<TeamMember> initExecutors() {
        return List.of(
                initTeamMember(1L, 1L, List.of(TeamRole.DEVELOPER, TeamRole.MANAGER), new ArrayList<>()),
                initTeamMember(2L, 2L, List.of(TeamRole.DESIGNER, TeamRole.OWNER), new ArrayList<>()));
    }

    private List<Team> initTeams() {
        List<Team> teams = new ArrayList<>();
        List<TeamMember> firstTeamMembers = List.of(
                initTeamMember(3L, 3L, List.of(TeamRole.DEVELOPER, TeamRole.INTERN), new ArrayList<>()));
        List<TeamMember> secondTeamMembers = List.of(
                initTeamMember(4L, 4L, List.of(TeamRole.DESIGNER, TeamRole.INTERN), new ArrayList<>()));
        teams.add(initTeam(1L, firstTeamMembers, project));
        teams.add(initTeam(2L, secondTeamMembers, project));
        return teams;
    }
}