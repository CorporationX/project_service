package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.exception.ProjectStatusException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private StageMapper stageMapper = Mappers.getMapper(StageMapper.class);

    @InjectMocks
    private StageService stageService;

    private static Long projectId;
    private static Project project = new Project();
    private static Stage stage = new Stage();
    private static Long stageId;
    private static Task task1 = new Task();
    private static Task task2 = new Task();

    @BeforeEach
    public void initialize() {
        projectId = 1000L;
        stageId = 555L;
        project.setId(projectId);
        stageService = new StageService(stageRepository, projectRepository, stageMapper);
        task1.setStage(stage);
        task2.setStage(stage);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        stage = Stage.builder()
                .stageId(stageId)
                .project(project)
                .tasks(tasks)
                .build();
    }

    @Test
    public void makeStageTestThrowsExceptionWhenProjectIsClosedOrCompleted() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        assertThrows(ProjectStatusException.class, () ->
                stageService.makeStage("stage", projectId, new ArrayList<>()));
        project.setStatus(ProjectStatus.COMPLETED);
        assertThrows(ProjectStatusException.class, () ->
                stageService.makeStage("stage", projectId, new ArrayList<>()));
        assertThrows(ProjectStatusException.class, () ->
                stageService.makeStage("stage", 0L, new ArrayList<>()));
    }

    @Test
    public void makeStageTest() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        stageService.makeStage("name", projectId, new ArrayList<>());
        ArgumentCaptor<Stage> argumentCaptor = ArgumentCaptor.forClass(Stage.class);
        verify(stageRepository, times(1)).save(argumentCaptor.capture());
    }

    @Test
    public void getStagesByStatusTest() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        Stage stage2 = new Stage();
        task1.setId(1L);
        task2.setId(2L);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.CANCELLED);
        stage.setTasks(List.of(task1));
        stage.setProject(project);
        stage2.setTasks(List.of(task2));
        stage2.setProject(project);
        when(stageRepository.findAll()).thenReturn(List.of(stage, stage2));
        List<StageDto> result = stageService.getStagesByStatus(projectId, TaskStatus.IN_PROGRESS);
        assertEquals(result.get(0).getProjectId(), projectId);
        assertEquals(result.size(), 1);
    }

    @Test
    public void deleteStageTestSetStatusCancelledTest() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        stageService.deleteStage(stageId);
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    public void deleteStageToOtherProjectTest() {
        Stage stage2 = new Stage();
        Long stage2Id = 999L;
        stage2.setStageId(stage2Id);
        stage2 = Stage.builder()
                .stageId(stage2Id)
                .tasks(new ArrayList<>())
                .build();
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageRepository.getById(stage2Id)).thenReturn(stage2);
        stageService.deleteStage(stageId, stage2.getStageId());
        assertEquals(task1.getStage().getStageId(), stage2Id);
    }

    @Test
    public void updateStageTest() {
        project.setTeams(List.of(new Team()));
        TeamMember teamMember1 = new TeamMember();
        TeamMember teamMember2 = new TeamMember();
        teamMember1.setRoles(List.of(TeamRole.ANALYST, TeamRole.DESIGNER));
        teamMember2.setRoles(List.of(TeamRole.ANALYST, TeamRole.DEVELOPER));
        List<TeamMember> teamMembers = List.of(teamMember1, teamMember2);
        Team team = new Team();
        stage.setExecutors(List.of(teamMember2));
        team.setTeamMembers(teamMembers);
        project.setTeams(List.of(team));
        StageRoles stageRoles = new StageRoles(222L, TeamRole.ANALYST, 2, stage);
        stage.setStageRoles(List.of(stageRoles));
        when(stageRepository.getById(stageId)).thenReturn(stage);
        stageService.updateStage(stageId);
        // здесь надо проверить сколько раз отправилось приглашение
    }

    @Test
    public void getAllStagesTest() {
        when(stageRepository.findAll()).thenReturn(List.of(stage));
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        List<StageDto> result = stageService.getAllStages(projectId);
        assertEquals(result.get(0).getProjectId(), projectId);
    }

    @Test
    public void getStageTest() {
        when(stageRepository.getById(100L)).thenReturn(new Stage());
        stageService.getStage(100L);
        verify(stageRepository, times(1)).getById(100L);
    }
}
