package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.dto.client.stage.StageFilterDTO;
import faang.school.projectservice.exception.stage.DataValidException;
import faang.school.projectservice.factory.DeletionStrategyFactory;
import faang.school.projectservice.mapper.StageCreateMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
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
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.Stage.CascadeDeleteStrategy;
import faang.school.projectservice.service.Stage.CloseTasksStrategy;
import faang.school.projectservice.service.Stage.MoveTasksStrategy;
import faang.school.projectservice.service.Stage.StageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static faang.school.projectservice.model.ProjectStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageCreateMapper stageCreateMapper;
    @Mock
    private StageRolesMapper stageRolesMapper;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private DeletionStrategyFactory deletionStrategyFactory;

    @InjectMocks
    private StageService stageService;
    private StageDtoCreate stageDtoCreate;
    private StageDTO stageDTO;
    private Project project;
    private Stage stage;
    private List<StageRoles> stageRoles;
    private Team team;

    @Test
    void PositiveCreate_ShouldCreateNewStage() {
        TeamMember creator = getCreator();
        project = getProject();
        stageDtoCreate = getStageDtoCreate();
        stage = getStage();
        stageRoles = getStageRoles();
        stageDTO = getStageDTO();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        when(stageCreateMapper.toEntity(stageDtoCreate)).thenReturn(stage);
        when(stageRolesMapper.mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage)).thenReturn(stageRoles);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDTO);

        StageDTO result = stageService.create(stageDtoCreate, creator.getId(), project.getId());

        verify(projectRepository).findById(project.getId());
        verify(stageCreateMapper).toEntity(stageDtoCreate);
        verify(stageRolesMapper).mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage);
        verify(stageRepository).save(stage);
        verify(stageRolesRepository).saveAll(stageRoles);
        verify(stageMapper).toDto(stage);

        Assertions.assertNotNull(result);
        assertThat(result).isEqualTo(stageDTO);
    }

    @Test
    void PositiveUpdate_ShouldUpdateAndReturnStageDTO() {
        List<Task> tasks = new ArrayList<>();
        stage = getStage();
        project = getProject();
        stageDTO = getStageDTO();

        when(stageRepository.findById(stageDTO.getId())).thenReturn(Optional.of(stage));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.findByIdIn(any())).thenReturn(tasks);
        when(stageRepository.save(any())).thenReturn(stage);
        doReturn(stageDTO).when(stageMapper).toDto(any(Stage.class));

        StageDTO result = stageService.update(stageDTO, Map.of());

        ArgumentCaptor<Stage> stageCaptor = ArgumentCaptor.forClass(Stage.class);
        verify(stageRepository).save(stageCaptor.capture());
        Stage capturedStage = stageCaptor.getValue();
        verify(stageMapper).toDto(capturedStage);

        Assertions.assertNotNull(result);
        assertThat(result).isEqualTo(stageDTO);
    }

    @Test
    void PositiveGetRoleAndStatus_ShouldReturnRoleAndStatus() {
        StageFilterDTO stageFilterDTO = getStageFilterDTO();
        List<Stage> stages = List.of(getStage());
        List<StageDTO> stageDTOs = List.of(getStageDTO());

        when(stageRepository.findStagesByRolesAndTaskStatus(
                stageFilterDTO.getTeamRoles(), stageFilterDTO.getTasksStatus()))
                .thenReturn(stages);
        when(stageMapper.toDtoList(stages)).thenReturn(stageDTOs);

        List<StageDTO> result = stageService.getRoleAndStatus(stageFilterDTO);

        verify(stageRepository).findStagesByRolesAndTaskStatus(
                stageFilterDTO.getTeamRoles(), stageFilterDTO.getTasksStatus());
        verify(stageMapper).toDtoList(stages);

        Assertions.assertNotNull(result);
        assertThat(result).isEqualTo(stageDTOs);
    }


    @Test
    void PositiveGetAllProjectStages_ShouldReturnAllProjectStagesDtoList() {
        Long projectId = 1L;
        Project project = getProject();
        List<Stage> stages = List.of(getStage());
        project.setStages(stages);
        List<StageDTO> expectedStageDTOs = List.of(getStageDTO());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageMapper.toDtoList(stages)).thenReturn(expectedStageDTOs);

        List<StageDTO> result = stageService.getAllProjectStages(projectId);

        verify(projectRepository).findById(projectId);
        verify(stageMapper).toDtoList(stages);

        Assertions.assertNotNull(result);
        assertThat(result).isEqualTo(expectedStageDTOs);
    }

    @Test
    void PositiveGetStage_ShouldReturnStageDTOById() {
        stage = getStage();
        stageDTO = getStageDTO();
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        when(stageMapper.toDto(stage)).thenReturn(stageDTO);

        StageDTO result = stageService.getStage(stage.getStageId());

        verify(stageRepository).findById(stage.getStageId());
        verify(stageMapper).toDto(stage);
        assertThat(result).isEqualTo(stageDTO);

    }

    @Test
    void PositiveDeleteCloseTasks_shouldDeleteStageWithCloseTasksStrategy() {
        stage = getStage();
        List<Task> tasks = getTasks();
        String strategy = "close_tasks";
        CloseTasksStrategy closeTasksStrategy = new CloseTasksStrategy(taskRepository, stageRepository);

        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        when(taskRepository.findByStage(stage)).thenReturn(tasks);
        when(deletionStrategyFactory.getStrategy(strategy)).thenReturn(closeTasksStrategy);

        stageService.deleteWithStrategy(stage.getStageId(), strategy, null);

        verify(taskRepository).findByStage(stage);
        verify(taskRepository).saveAll(tasks);
        verify(stageRepository).delete(stage);
        assertEquals(TaskStatus.CANCELLED, tasks.get(0).getStatus());
    }

    @Test
    void PositiveCascadeDelete_shouldDeleteStageWithCascadeDeleteStrategy() {
        stage = getStage();
        String strategy = "cascade_delete";
        CascadeDeleteStrategy cascadeDeleteStrategy = new CascadeDeleteStrategy(taskRepository, stageRepository);

        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        when(deletionStrategyFactory.getStrategy(strategy)).thenReturn(cascadeDeleteStrategy);

        stageService.deleteWithStrategy(stage.getStageId(), strategy, null);

        verify(taskRepository).deleteByStage(stage);
        verify(stageRepository).delete(stage);
    }

    @Test
    void PositiveMoveDelete_shouldMoveTasksAndDeleteStageWithMoveTasksStrategy() {
        stage = getStage();
        Stage targetStage = getStage();
        List<Task> tasks = getTasks();
        targetStage.setStageId(2L);

        String strategy = "move_tasks";

        MoveTasksStrategy moveTasksStrategy = new MoveTasksStrategy(taskRepository, stageRepository);
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        when(stageRepository.findById(targetStage.getStageId())).thenReturn(Optional.of(targetStage));
        when(taskRepository.findByStage(stage)).thenReturn(tasks);
        when(deletionStrategyFactory.getStrategy(strategy)).thenReturn(moveTasksStrategy);

        stageService.deleteWithStrategy(stage.getStageId(), strategy, targetStage.getStageId());

        verify(taskRepository).findByStage(stage);
        verify(taskRepository).saveAll(tasks);
        verify(stageRepository).delete(stage);
        assertEquals(targetStage, tasks.get(0).getStage());
    }

    @Test
    void NegativeCreate_shouldThrowExceptionProjectNotFound() {
        Long projectId = 2L;
        Long creatorId = 1L;
        StageDtoCreate stageDtoCreate = getStageDtoCreate();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                stageService.create(stageDtoCreate, creatorId, projectId)
        );
        assertEquals("Project not found with ID: " + projectId, ex.getMessage());
    }

    @Test
    void NegativeCreate_shouldThrowExceptionTeamMemberNotFound() {
        project = getProject();
        Long creatorId = 11L;
        StageDtoCreate stageDtoCreate = getStageDtoCreate();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                stageService.create(stageDtoCreate, creatorId, project.getId())
        );
        assertEquals("TeamMember not found with ID: " + creatorId, ex.getMessage());
    }

    @Test
    void NegativeCreate_shouldThrowExceptionCreatorHasNoPermission() {
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(DESIGNER));
        project = getProject();
        Long creatorId = 11L;
        StageDtoCreate stageDtoCreate = getStageDtoCreate();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.of(teamMember));

        DataValidException ex = assertThrows(DataValidException.class, () ->
                stageService.create(stageDtoCreate, creatorId, project.getId())
        );
        Assertions.assertTrue(ex.getMessage().contains("Don`t have permission"));
    }

    @Test
    void NegativeCreate_shouldThrowExceptionStageDtoCreateIsNull() {
        project = getProject();
        TeamMember teamMember = getCreator();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.of(teamMember));

        DataValidException ex = assertThrows(DataValidException.class, () ->
                stageService.create(null, teamMember.getId(), project.getId())
        );
        assertEquals("Data not valid", ex.getMessage());
    }

    @Test
    void NegativeCreate_shouldThrowExceptionProjectStatusIsCompleted() {
        project = getProject();
        project.setStatus(ProjectStatus.COMPLETED);
        TeamMember teamMember = getCreator();
        Long creatorId = 11L;
        StageDtoCreate stageDtoCreate = getStageDtoCreate();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.of(teamMember));

        DataValidException ex = assertThrows(DataValidException.class, () ->
                stageService.create(stageDtoCreate, creatorId, project.getId())
        );
        assertEquals("Project status is COMPLETED", ex.getMessage());
    }

    @Test
    void NegativeUpdate_shouldThrowExceptionStageNotFound() {
        stageDTO = getStageDTO();
        Map<String, String> roleAndCount = Map.of("1","DEVELOPER");
        when(stageRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                stageService.update(stageDTO, roleAndCount)
        );
        assertEquals("Stage not found", ex.getMessage());
    }

    @Test
    void NegativeUpdate_shouldThrowExceptionProjectNotFound() {
        stage = getStage();
        stageDTO = getStageDTO();
        Map<String, String> roleAndCount = Map.of("1","DEVELOPER");
        when(stageRepository.findById(anyLong())).thenReturn(Optional.of(stage));
        when(taskRepository.findByIdIn(anyList())).thenReturn(List.of());
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                stageService.update(stageDTO, roleAndCount)
        );
        assertEquals("Project not found", ex.getMessage());
    }
    @Test
    void NegativeGetRoleAndStatus_shouldReturnEmptyListNoMatchingStages() {
        when(stageRepository.findStagesByRolesAndTaskStatus(anyList(), anyList())).thenReturn(List.of());

        List<StageDTO> result = stageService.getRoleAndStatus(new StageFilterDTO(List.of(), List.of()));
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void NegativeDelete_shouldThrowExceptionStrategyIsNullOrEmpty() {
        assertThrows(DataValidException.class, () -> stageService.deleteWithStrategy(1L, null, null));
        assertThrows(DataValidException.class, () -> stageService.deleteWithStrategy(1L, " ", null));
    }

    @Test
    void NegativeDelete_shouldThrowExceptionStageNotFound() {
        stage = getStage();
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stageService.deleteWithStrategy(stage.getStageId(), "cascade_delete", null));
    }

    @Test
    void NegativeDelete_shouldThrowExceptionStrategyRequiresTargetButNoneProvided() {
        stage = getStage();
        String strategy = "move_tasks";
        MoveTasksStrategy moveTasksStrategy = new MoveTasksStrategy(taskRepository, stageRepository);

        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        when(deletionStrategyFactory.getStrategy(strategy)).thenReturn(moveTasksStrategy);

        assertThrows(IllegalArgumentException.class, () -> stageService.deleteWithStrategy(stage.getStageId(), strategy, null));
    }

    @Test
    void NegativeDelete_shouldThrowExceptionTargetStageNotFound() {
        stage = getStage();
        Stage targetStage = getStage();
        targetStage.setStageId(2L);
        String strategy = "move_tasks";
        MoveTasksStrategy moveTasksStrategy = new MoveTasksStrategy(taskRepository, stageRepository);

        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        when(stageRepository.findById(targetStage.getStageId())).thenReturn(Optional.empty());
        when(deletionStrategyFactory.getStrategy(strategy)).thenReturn(moveTasksStrategy);

        assertThrows(EntityNotFoundException.class, () -> stageService.deleteWithStrategy(stage.getStageId(), strategy, targetStage.getStageId()));
    }

    @Test
    void NegativeGetAllProjectStages_ShouldThrowException_WhenProjectNotFound() {
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.getAllProjectStages(projectId));

        assertThat(exception.getMessage()).isEqualTo("Project not found with ID: " + projectId);
        verify(projectRepository).findById(projectId);
        verifyNoInteractions(stageMapper);
    }

    @Test
    void NegativeGetStage_ShouldThrowExceptionStageNotFound() {
        Long stageId = 1L;
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.getStage(stageId));

        assertThat(exception.getMessage()).isEqualTo("Stage not found with ID: " + stageId);
        verify(stageRepository).findById(stageId);
        verifyNoInteractions(stageMapper);
    }

    private StageDtoCreate getStageDtoCreate() {
        HashMap<TeamRole, Integer> teamRoles = new HashMap<>();
        teamRoles.put(OWNER, 1);
        stageDtoCreate =
                StageDtoCreate.builder()
                        .id(1L)
                        .stageName("Test")
                        .roleAndCount(teamRoles)
                        .build();
        return stageDtoCreate;
    }

    private Project getProject() {
        team = getTeam();
        List<Team> validTeams = List.of(team);
        return project = Project.builder()
                .id(1L)
                .name("test")
                .stages(new ArrayList<>())
                .status(IN_PROGRESS)
                .teams(validTeams)
                .build();
    }

    private Stage getStage() {
        if (project == null) {
            project = getProject();
        }
        List<Task> validTasks = getTasks();
        stage = Stage.builder()
                .stageId(1L)
                .stageName("Test")
                .tasks(validTasks)
                .executors(new ArrayList<>())
                .project(project)
                .build();
        return stage;
    }

    private List<StageRoles> getStageRoles() {
        stageRoles = new ArrayList<>();
        StageRoles stageRole = new StageRoles();
        stageRole.setId(1L);
        stageRole.setTeamRole(OWNER);
        stageRole.setCount(1);
        stageRole.setStage(stage);
        stageRoles.add(stageRole);
        return stageRoles;
    }

    private StageDTO getStageDTO() {
        List<Long> ids = List.of(1L, 2L, 3L);
        return stageDTO = StageDTO.builder()
                .id(1L)
                .stageName("Test")
                .stageRoleIds(ids)
                .executorsIds(ids)
                .tasksIds(ids)
                .projectId(1L)
                .build();
    }

    private TeamMember getCreator() {
        List<TeamRole> validRoles = List.of(MANAGER, OWNER);
        return TeamMember.builder()
                .id(1L)
                .nickname("test")
                .roles(validRoles)
                .build();
    }

    private Team getTeam() {
        List<TeamMember> teamMembers = getTeamMembers();
        return team = Team.builder()
                .id(1L)
                .teamMembers(teamMembers)
                .project(project)
                .build();
    }

    private List<TeamMember> getTeamMembers() {
        List<TeamRole> validRoles = List.of(DEVELOPER, DESIGNER);
        TeamMember teamMember1 = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .nickname("test1")
                .team(team)
                .roles(validRoles)
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .id(2L)
                .userId(2L)
                .nickname("test2")
                .team(team)
                .roles(validRoles)
                .build();

        return List.of(teamMember1, teamMember2);
    }

    private List<Task> getTasks() {
        Task task1 = Task.builder()
                .id(1L)
                .stage(stage)
                .project(project)
                .status(TaskStatus.IN_PROGRESS)
                .build();
        Task task2 = Task.builder()
                .id(2L)
                .stage(stage)
                .project(project)
                .status(TaskStatus.TESTING)
                .build();
        return List.of(task1, task2);
    }

    private StageFilterDTO getStageFilterDTO() {
        StageFilterDTO stageFilterDTO = new StageFilterDTO();
        stageFilterDTO.setTeamRoles(List.of(DEVELOPER, MANAGER));
        stageFilterDTO.setTasksStatus(List.of(TaskStatus.IN_PROGRESS, TaskStatus.TESTING));
        return stageFilterDTO;
    }


}