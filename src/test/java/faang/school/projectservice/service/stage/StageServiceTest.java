package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.mapper.stage.StageRolesMapperImpl;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    private StageDto stageDto;
    private Project project;
    private List<Team> teams;
    private Team team;
    private Team teamOne;
    private TeamMember teamMember;
    private TeamMember teamMember1;
    private TeamMember teamMemberTwo;
    private TeamMember teamMemberThree;

    private StageRolesDto stageRolesDto;
    private Stage stage;
    private Stage stageOne;
    private List<Task> tasks;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);

        stage = Stage.builder()
                .stageId(1L)
                .stageName("Name")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CANCELLED)
                        .build())
                .executors(new ArrayList<>(List.of(TeamMember.builder()
                        .id(1L)
                        .stages(new ArrayList<>(List.of(Stage.builder().stageId(1L).build())))
                        .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                        .build(),
                        TeamMember.builder()
                                .id(2L)
                                .stages(new ArrayList<>(List.of(Stage.builder().stageId(1L).build())))
                                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                                .build()
                        )))
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .stages(new ArrayList<>(List.of(Stage.builder().stageId(1L).build())))
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();

        teamMember1 = TeamMember.builder()
                .id(2L)
                .stages(new ArrayList<>(List.of(Stage.builder().stageId(1L).build())))
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        team = Team.builder()
                .teamMembers(List.of(teamMember1,teamMember))
                .build();

        StageRolesDto stageRolesDto = StageRolesDto.builder()
                .teamRole(TeamRole.DEVELOPER)
                .count(1)
                .build();
        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("Name")
                .projectId(1L)
                .stageRoles(List.of(stageRolesDto))
                .build();


        project = Project.builder()
                .id(1L)
                .ownerId(1L)
                .teams(List.of(team))
                .build();

        tasks = new ArrayList<>();
        tasks.add(Task.builder()
                .name("task")
                .status(TaskStatus.TODO)
                .build());

        stageOne = Stage.builder()
                .stageId(1L)
                .stageName("Name")
                .tasks(tasks)
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
    public void testUpdateStage_IfHasAllExecutorsInStage() {
        stageRolesDto = StageRolesDto.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(2)
                .build();

        stageDto.setStageRoles(List.of(stageRolesDto));
        stageDto.setExecutorIds(List.of(1L, 2L));

        stage.setStageRoles(new ArrayList<>(List.of(StageRoles.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(1)
                .build())));

        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);

        StageDto createdStageDto = stageService.updateStage(1L, stageRolesDto);

        Mockito.verify(stageRepository, Mockito.times(1)).save(stage);
        Assertions.assertEquals(stageDto, createdStageDto);
    }
}