package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDeleteTaskStrategyDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.task.TasksHandlingStrategy;
import faang.school.projectservice.dto.teamrole.TeamRoleDto;
import faang.school.projectservice.filter.stagefilter.StageFilter;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validation.StageValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskService taskService;
    @Mock
    private StageFilter taskStatusFilter;
    @Mock
    private StageFilter teamRoleFilter;
    @Mock
    private StageValidator stageValidator;
    @Mock
    private StageRolesRepository stageRolesRepository;

    private Long stageId;
    private Long newStageId;
    private Long projectId;
    private Stage stage;
    private Stage stageNew;
    private StageDto stageDto;
    private StageFilterDto stageFilterDto;
    @Mock
    private TeamRoleDto teamRoleDto;
    @Mock
    private StageDeleteTaskStrategyDto strategyDto;
    private Project project;
    private List<Stage> stages;
    private List<StageDto> stageDtos;
    private List<StageFilter> stageFilters;
    private List<Task> tasks;

    @InjectMocks
    private StageService stageService;

    @BeforeEach
    void setup() {
        stageId = 1L;
        newStageId = 2L;
        projectId = 3L;
        Task task = new Task();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        StageRoles stageRoles = new StageRoles();
        List<StageRoles> stageRolesList = new ArrayList<>();
        List<TeamMember> teamMemberList = new ArrayList<>();
        List<Team> teamList = new ArrayList<>();
        List<Task> tasksNew = new ArrayList<>();
        strategyDto = new StageDeleteTaskStrategyDto();
        tasks = new ArrayList<>();
        stageDtos = new ArrayList<>();
        stageDto = new StageDto();
        stageFilterDto = new StageFilterDto();
        project = new Project();
        stage = new Stage();
        teamRoleDto = new TeamRoleDto();
        stage.setStageId(stageId);
        project.setStages(stages);
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        teamMemberList.add(teamMember);
        team.setTeamMembers(teamMemberList);
        teamList.add(team);
        stageNew = new Stage();
        stageDtos.add(stageDto);
        tasks.add(task);
        stage.setTasks(tasks);
        stage.setProject(project);
        stage.setExecutors(teamMemberList);
        stageNew.setTasks(tasksNew);
        stageRoles.setId(stageId);
        stageRoles.setStage(stage);
        stageRoles.setCount(5);
        stageRolesList.add(stageRoles);
        stageRolesList.add(stageRoles);
        stageRolesList.add(stageRoles);
        stage.setStageRoles(stageRolesList);
        stageRoles.setTeamRole(TeamRole.ANALYST);
        teamRoleDto.setRolePattern(TeamRole.ANALYST);
        project.setId(projectId);
        stageFilters = List.of(teamRoleFilter, taskStatusFilter);
        stages = List.of(stage);
        project.setTeams(teamList);
        teamMember.setStages(stages);
        stageService = new StageService(stageRepository,
                stageMapper,
                projectService,
                taskService,
                stageFilters,
                stageValidator,
                stageRolesRepository);
    }

    @Test
    void testCreateStage() {
        stage.setStageRoles(new ArrayList<>());
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);

        stageService.createStage(stageDto);

        verify(stageValidator, times(1)).validationProjectById(stageDto.getProjectId());
        verify(stageMapper, times(1)).toEntity(stageDto);
        verify(stageRepository, times(1)).save(stage);
        verify(stageRolesRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testFilterStagesWithApplicableTrue() {
        when(stageValidator.getStages(projectId)).thenReturn(stages);
        when(stageFilters.get(0).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(1).isApplicable(any())).thenReturn(true);
        when(stageFilters.get(0).apply(any(), any())).thenReturn(Stream.of(stage));
        when(stageFilters.get(1).apply(any(), any())).thenReturn(Stream.of(stage));
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        List<StageDto> stageDtos = stageService.filterStages(projectId, stageFilterDto);

        verify(stageValidator, times(1)).validationProjectById(projectId);
        verify(stageValidator, times(1)).getStages(projectId);
        verify(stageMapper, times(1)).toDto(stage);
        verify(stageFilters.get(0), times(1)).isApplicable(any());
        verify(stageFilters.get(1), times(1)).isApplicable(any());
        verify(stageFilters.get(0), times(1)).apply(any(), any());
        verify(stageFilters.get(1), times(1)).apply(any(), any());
        assertEquals(1, stageDtos.size());
        assertEquals(stageDto, stageDtos.get(0));
    }

    @Test
    void testFilterStagesWithNoApplicableFalse() {
        when(stageValidator.getStages(projectId)).thenReturn(stages);
        when(stageFilters.get(0).isApplicable(stageFilterDto)).thenReturn(false);
        when(stageFilters.get(1).isApplicable(stageFilterDto)).thenReturn(false);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        List<StageDto> stageDtos = stageService.filterStages(projectId, stageFilterDto);

        verify(stageValidator, times(1)).validationProjectById(projectId);
        verify(stageValidator, times(1)).getStages(projectId);
        verify(stageMapper, times(1)).toDto(stage);
        verify(stageFilters.get(0), times(1)).isApplicable(any());
        verify(stageFilters.get(1), times(1)).isApplicable(any());
        assertEquals(1, stageDtos.size());
    }

    @Test
    void testDeleteStageCascadeDelete() {
        strategyDto.setStrategy(TasksHandlingStrategy.CASCADE_DELETE);
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(taskService.findAllByStage(stage)).thenReturn(tasks);
        stageService.deleteStage(stageId, strategyDto, newStageId);
        verify(stageRepository, times(1)).getById(stageId);
        verify(taskService, times(1)).findAllByStage(stage);
        verify(stageRepository, times(1)).delete(any(Stage.class));
    }

    @Test
    void testDeleteStageCloseTask() {
        strategyDto.setStrategy(TasksHandlingStrategy.CLOSE_TASKS);
        when(stageRepository.getById(stageId)).thenReturn(stage);
        stageService.deleteStage(stageId, strategyDto, newStageId);
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).delete(any(Stage.class));
    }

    @Test
    void testDeleteStageMoveTasks() {
        strategyDto.setStrategy(TasksHandlingStrategy.MOVE_TASKS);
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageRepository.getById(newStageId)).thenReturn(stageNew);
        stageService.deleteStage(stageId, strategyDto, newStageId);
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).getById(newStageId);
        verify(stageRepository, times(1)).delete(any(Stage.class));
    }

    @Test
    void testGetAllStagesByProjectId() {
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(stageMapper.toDtos(project.getStages())).thenReturn(stageDtos);
        stageService.getAllStageByProjectId(projectId);
        verify(projectService, times(1)).getProjectById(projectId);
        verify(stageMapper, times(1)).toDtos(project.getStages());
    }

    @Test
    void testGetStageById() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);
        stageService.getStageById(stageId);
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageMapper, times(1)).toDto(stage);
    }

    @Test
    void testUpdateStageWithTeamMembers() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        stageService.updateStageWithTeamMembers(stageId, teamRoleDto);
        verify(stageRepository, atLeastOnce()).getById(stageId);
        verify(projectService, atLeastOnce()).getProjectById(projectId);

    }


}