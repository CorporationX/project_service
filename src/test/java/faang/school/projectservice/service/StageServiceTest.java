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
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;

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



    private final Long stageId = 1L;
    private final Long newStageId = 2L;
    private final Long projectId = 1L;

    private Stage stageNew; // а надо ли?
    private StageDto stageDto;
    private StageFilterDto stageFilterDto;
    private TeamRoleDto teamRoleDto;
    private StageDeleteTaskStrategyDto strategyDto;
    private Project project;
    private Stage stage;
    private List<Stage> stages;
    private List<StageDto> stageDtos;
    private List<StageFilter> stageFilters;
    private List<Task> tasks;

    @InjectMocks
    private StageService stageService;

    @BeforeEach
    void setup() {
        ServiceTestingHelper testingHelper = new ServiceTestingHelper();
        stageDto = testingHelper.stageDto();
        teamRoleDto = testingHelper.teamRoleDto();
        project = testingHelper.project();
        stage = testingHelper.stageFirst();
        stages = testingHelper.stages();
        stageDtos = testingHelper.stageDtos();
        tasks = testingHelper.tasks();
        strategyDto = new StageDeleteTaskStrategyDto();
        stageFilterDto = new StageFilterDto();
        List<Task> tasksNew = new ArrayList<>(); // а надо ли?
        stageNew = new Stage();// а надо ли?
        stageNew.setTasks(tasksNew);// а надо ли?
        stageFilters = List.of(teamRoleFilter, taskStatusFilter);
        stageService = new StageService(stageRepository,
                stageMapper,
                projectService,
                taskService,
                stageFilters,
                stageValidator,
                stageRolesRepository);
    }

    @Test
    void testCreateStageEntity() {
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);

        stageService.createStageEntity(stageDto);
    }

    @Test
    void testCreateStage() {
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        stageService.createStage(stageDto);
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

        assertEquals(1, stageDtos.size());
        assertEquals(stageDto, stageDtos.get(0));
    }

    @Test
    void testFilterStagesWithNoApplicableFalse() {
        when(stageValidator.getStages(projectId)).thenReturn(stages);
        when(stageFilters.get(0).isApplicable(any())).thenReturn(false);
        when(stageFilters.get(1).isApplicable(any())).thenReturn(false);
//        when(stageMapper.toDto(any())).thenReturn(any());
        when(stageMapper.toDto(stage)).thenReturn(stageDto); //почему тут SOFE

        List<StageDto> stageDtos = stageService.filterStages(projectId, stageFilterDto);

        assertEquals(1, stageDtos.size());
    }

    @Test
    void testDeleteStageCascadeDelete() {
        strategyDto.setStrategy(TasksHandlingStrategy.CASCADE_DELETE);
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(taskService.findAllByStage(stage)).thenReturn(tasks);
        stageService.deleteStage(stageId,strategyDto,newStageId);
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