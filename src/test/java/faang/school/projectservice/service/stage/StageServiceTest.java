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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Stage stage;
    private Stage replaceStage;
    private StageDto stageDto;
    private Long id;
    private Long replaceId;
    private StageRoles stageRoles;
    private TeamMember teamMember;
    private TeamMember storTeamMember;
    private List<TeamMember> executors;
    private List<TeamMember> teamMembers;
    private List<StageRoles> stageRolesList;
    private List<Stage> stages;
    private List<StageDto> stageDtos;
    private StageFilterDto stageFilterDto;
    private Project project;
    private List<StageFilter> stageFilters;
    private StageFilter stageStatusFilter;
    private StagePreDestroyAction stagePreDestroyAction;
    private List<Task> tasks;
    private List<Task> tasksReplaced;
    private Task task;
    private Team team;
    private List<Team> teams;
    private StageInvitation stageInvitation;

    @BeforeEach
    public void setUp() {
        id = 1L;
        replaceId = 2L;

        stageInvitation = new StageInvitation();
        stageDto = new StageDto();
        stageRoles = StageRoles
                .builder()
                .count(2)
                .teamRole(TeamRole.OWNER)
                .build();
        stageFilterDto = new StageFilterDto();
        storTeamMember = TeamMember
                .builder()
                .roles(List.of(TeamRole.OWNER))
                .build();
        teamMembers = new ArrayList<>();
        teamMembers.add(storTeamMember);

        team = Team
                .builder()
                .teamMembers(teamMembers)
                .build();

        teams = new ArrayList<>();
        teams.add(team);
        project = Project
                .builder()
                .teams(teams)
                .build();
        stageStatusFilter = Mockito.mock(StageStatusFilter.class);
        task = Task
                .builder()
                .id(id)
                .build();

        teamMember = TeamMember
                .builder()
                .roles(List.of(TeamRole.OWNER))
                .build();

        executors = new ArrayList<>();
        executors.add(teamMember);
        stages = new ArrayList<>();
        stageRolesList = new ArrayList<>();
        stageRolesList.add(stageRoles);
        stageFilters = new ArrayList<>();
        stageDtos = new ArrayList<>();

        tasks = new ArrayList<>();
        tasksReplaced = new ArrayList<>();
        tasks.add(task);
        tasksReplaced.add(task);

        stagePreDestroyAction = StagePreDestroyAction.REMOVE;
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
    public void testCreate() {
        stageDto.setStageId(id);

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
    public void testGetStages() {
        project.setStages(stages);
        Stream<Stage> stageStream = stages.stream();
        stages.add(stage);
        stageDtos.add(stageDto);
        stageFilters.add(stageStatusFilter);

        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(stageFiltersMock.stream()).thenReturn(stageFilters.stream());
        when(stageStatusFilter.isApplicable(any())).thenReturn(true);
        when(stageStatusFilter.apply(any(), any())).thenReturn(stageStream);
        when(stageMapper.toDto(any())).thenReturn(stageDto);

        List<StageDto> result = stageService.getStages(id, stageFilterDto);

        verify(projectRepository).getProjectById(id);
        verify(stageFiltersMock).stream();
        verify(stageStatusFilter).isApplicable(any());
        verify(stageStatusFilter).apply(any(), any());
        verify(stageMapper).toDto(any());

        assertEquals(result, stageDtos);
    }

    @Test
    public void testRemoveStageRemove() {
//        stagePreDestroyAction = StagePreDestroyAction.REMOVE;
//        stage.setTasks(tasks);

        when(stageRepository.getById(id)).thenReturn(stage);
        doNothing().when(taskRepository).deleteById(task.getId());
        doNothing().when(stageRepository).delete(stage);

        stageService.removeStage(id, stagePreDestroyAction);

        verify(stageRepository).getById(id);
        verify(taskRepository).deleteById(task.getId());
        verify(stageRepository).delete(stage);
    }

    @Test
    public void testRemoveStageDone() {
//        stagePreDestroyAction = StagePreDestroyAction.DONE;
//        stage.setTasks(tasks);

        when(stageRepository.getById(id)).thenReturn(stage);
        doNothing().when(stageRepository).delete(stage);

        stageService.removeStage(id, stagePreDestroyAction);

        verify(stageRepository).getById(id);
        verify(stageRepository).delete(stage);
    }

    @Test
    public void testRemoveStageReplace() {
        doNothing().when(stageIdValidator).validateReplaceId(anyLong(), anyLong());
        when(stageRepository.getById(id)).thenReturn(stage);
        when(stageRepository.getById(replaceId)).thenReturn(replaceStage);
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);
        doNothing().when(stageRepository).delete(stage);

        stageService.removeStage(id, replaceId);

        verify(stageIdValidator).validateReplaceId(anyLong(), anyLong());
        verify(stageRepository).getById(id);
        verify(stageRepository).getById(replaceId);
        verify(stageRepository).save(any(Stage.class));
        verify(stageRepository).delete(stage);
    }

    @Test
    public void testUpdateStage() {
        when(stageRepository.getById(id)).thenReturn(stage);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        stageService.updateStage(id);

        verify(stageRepository).getById(id);
    }
}
