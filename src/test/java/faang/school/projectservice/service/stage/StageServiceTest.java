package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.filter.stage.StageStatusFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.StagePreDestroyAction;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageDtoValidator;
import faang.school.projectservice.validator.stage.StageIdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageServiceTest {
    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageDtoValidator stageDtoValidator;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private List<StageFilter> stageFiltersMock;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StageIdValidator stageIdValidator;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    private Long id;
    private Long replaceId;
    private Stage stage;
    private Stage replaceStage;
    private Project project;
    private Task task;
    private StageDto stageDto;
    private StageFilterDto stageFilterDto;
    private StageFilter stageStatusFilter;
    private StageInvitation stageInvitation;

    private Stream<Stage> stageStream;
    private List<StageFilter> stageFilters;
    private List<StageDto> stageDtos;
    private List<Task> tasks;

    @BeforeEach
    public void setUp() {
        id = 1L;
        replaceId = 2L;

        stageInvitation = new StageInvitation();
        stageFilterDto = new StageFilterDto();
        stageStatusFilter = Mockito.mock(StageStatusFilter.class);
        stage = new Stage();

        stageFilters = new ArrayList<>(Collections.singletonList(stageStatusFilter));

        stageDto = StageDto
                .builder()
                .stageId(id)
                .build();

        stageDtos = new ArrayList<>(Collections.singletonList(stageDto));

        StageRoles stageRoles = StageRoles
                .builder()
                .count(2)
                .teamRole(TeamRole.OWNER)
                .build();

        TeamMember teamMember = TeamMember
                .builder()
                .roles(List.of(TeamRole.OWNER))
                .build();

        List<TeamMember> executors = new ArrayList<>(Collections.singletonList(teamMember));
        List<StageRoles> stageRolesList = new ArrayList<>(Collections.singletonList(stageRoles));

        TeamMember storTeamMember = TeamMember
                .builder()
                .roles(List.of(TeamRole.OWNER))
                .build();

        List<TeamMember> teamMembers = new ArrayList<>(Collections.singletonList(storTeamMember));

        Team team = Team
                .builder()
                .teamMembers(teamMembers)
                .build();

        List<Team> teams = new ArrayList<>(Collections.singletonList(team));

        task = Task
                .builder()
                .id(id)
                .build();

        tasks = new ArrayList<>(Collections.singletonList(task));
        List<Task> tasksReplaced = new ArrayList<>(Collections.singletonList(task));

        List<Stage> stages = new ArrayList<>(Collections.singletonList(stage));

        stageStream = stages.stream();

        project = Project
                .builder()
                .stages(stages)
                .teams(teams)
                .build();

        stage = Stage
                .builder()
                .project(project)
                .executors(executors)
                .stageRoles(stageRolesList)
                .tasks(tasks)
                .build();

        replaceStage = Stage
                .builder()
                .tasks(tasksReplaced)
                .build();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("testing that create() calls all his beans correctly + testing return value")
    public void testCreate() {
        doNothing().when(stageDtoValidator).validateProjectId(id);
        doNothing().when(stageDtoValidator).validateStageRolesCount(any());
        when(stageMapper.toEntity(any(), any())).thenReturn(stage);
        when(stageRepository.save(any())).thenReturn(stage);
        when(stageMapper.toDto(any())).thenReturn(stageDto);

        StageDto result = stageService.create(stageDto);

        verify(stageDtoValidator).validateProjectId(id);
        verify(stageDtoValidator).validateStageRolesCount(any());
        verify(stageMapper).toEntity(any(), any());
        verify(stageRepository).save(any());
        verify(stageMapper).toDto(any());

        assertEquals(result, stageDto);
    }

    @Test
    @DisplayName("testing that getStages() calls all his beans correctly + testing return value")
    public void testGetStagesByFilter() {
        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(stageFiltersMock.stream()).thenReturn(stageFilters.stream());
        when(stageStatusFilter.isApplicable(any())).thenReturn(true);
        when(stageStatusFilter.apply(any(), any())).thenReturn(stageStream);
        when(stageMapper.toDto(any())).thenReturn(stageDto);

        List<StageDto> result = stageService.getStagesByFilter(id, stageFilterDto);

        verify(projectRepository).getProjectById(id);
        verify(stageFiltersMock).stream();
        verify(stageStatusFilter).isApplicable(any());
        verify(stageStatusFilter).apply(any(), any());
        verify(stageMapper).toDto(any());

        assertEquals(result, stageDtos);
    }

    @Test
    @DisplayName("testing that removeStage() calls all his beans correctly when StagePreDestroyAction.REMOVE was given")
    public void testRemoveStageRemove() {
        when(stageRepository.getById(id)).thenReturn(stage);
        doNothing().when(taskRepository).deleteAllById(tasks.stream().map(Task::getId).toList());
        doNothing().when(stageRepository).delete(stage);

        stageService.removeStage(id, StagePreDestroyAction.REMOVE);

        verify(stageRepository).getById(id);
        verify(taskRepository).deleteAllById(tasks.stream().map(Task::getId).toList());
        verify(stageRepository).delete(stage);
    }

    @Test
    @DisplayName("testing that removeStage() calls all his beans correctly when StagePreDestroyAction.DONE was given")
    public void testRemoveStageDone() {
        when(stageRepository.getById(id)).thenReturn(stage);
        doNothing().when(stageRepository).delete(stage);

        stageService.removeStage(id, StagePreDestroyAction.DONE);

        verify(stageRepository).getById(id);
        verify(taskRepository, Mockito.times(0)).deleteById(task.getId());
        verify(stageRepository).delete(stage);
    }

    @Test
    @DisplayName("testing that removeStage() calls all his beans correctly when replaceId was given")
    public void testRemoveStageAndReplaceTasks() {
        doNothing().when(stageIdValidator).validateReplaceId(anyLong(), anyLong());
        when(stageRepository.getById(id)).thenReturn(stage);
        when(stageRepository.getById(replaceId)).thenReturn(replaceStage);
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);
        doNothing().when(stageRepository).delete(stage);

        stageService.removeStageAndReplaceTasks(id, replaceId);

        verify(stageIdValidator).validateReplaceId(anyLong(), anyLong());
        verify(stageRepository).getById(id);
        verify(stageRepository).getById(replaceId);
        verify(stageRepository).save(any(Stage.class));
        verify(stageRepository).delete(stage);
    }

    @Test
    @DisplayName("testing that removeStage() calls all his beans correctly")
    public void testUpdateStage() {
        when(stageRepository.getById(id)).thenReturn(stage);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        stageService.updateStage(id);

        verify(stageRepository).getById(id);
        verify(stageInvitationRepository).save(any(StageInvitation.class));
    }

    @Test
    @DisplayName("testing that getAllStages() calls all his beans correctly + testing return value")
    public void testGetAllStages() {
        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(stageMapper.toDto(any(Stage.class))).thenReturn(stageDto);

        List<StageDto> result = stageService.getAllStages(id);

        verify(projectRepository).getProjectById(id);
        verify(stageMapper).toDto(any(Stage.class));

        assertEquals(result, stageDtos);
    }

    @Test
    @DisplayName("testing that getStage() calls all his beans correctly + testing return value")
    public void testGetStage() {
        when(stageRepository.getById(id)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStage(id);

        verify(stageRepository).getById(id);
        verify(stageMapper).toDto(stage);

        assertEquals(result, stageDto);
    }
}
