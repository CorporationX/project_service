package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.SubtaskActionDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.mapper.StageRolesMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
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

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Mock
    private StageInvitationService stageInvitationService;

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    @Spy
    private StageMapperImpl stageMapper;

    @Mock
    private StageValidator stageValidator;

    @Mock
    private UserContext userContext;

    private Stage stage1;

    private Stage stage4;

    private StageDto stageDto1;

    private StageRolesDto stageRolesDto;

    private TeamMember teamMember1;
    private TeamMember teamMember2;
    private TeamMember teamMember3;

    private TeamMember teamMember4;

    private Stage stageGetById;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);

        teamMember4 = TeamMember
                .builder()
                .id(4L)
                .roles(List.of(
                        TeamRole.DEVELOPER,
                        TeamRole.ANALYST
                ))
                .stages(List.of(Stage.builder().stageId(5L).build()))
                .build();
        Project project = Project
                .builder()
                .id(1L)
                .build();
        stage1 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(project)
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.DEVELOPER)
                        .count(1)
                        .build()))
                .build();
        Stage stage2 = Stage
                .builder()
                .stageId(2L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CANCELLED).build())
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
                .stageId(4L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
                .stageRoles(List.of())
                .tasks(tasks)
                .build();
        stageDto1 = stageMapper.toDto(stage1);
        stageRolesDto = StageRolesDto.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(2)
                .build();
        List<Stage> stages1 = new ArrayList<>();
        stages1.add(stage1);
        List<Stage> stages2 = new ArrayList<>();
        stages2.add(stage2);
        teamMember1 = TeamMember.builder()
                .id(1L)
                .roles(List.of(
                        TeamRole.DEVELOPER,
                        TeamRole.ANALYST
                ))
                .stages(stages1)
                .build();
        teamMember2 = TeamMember.builder()
                .id(1L)
                .roles(List.of(
                        TeamRole.MANAGER
                ))
                .stages(stages2)
                .build();
        teamMember3 = TeamMember.builder()
                .id(1L)
                .roles(List.of(
                        TeamRole.DEVELOPER
                ))
                .stages(stages2)
                .build();

        stageGetById = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(project)
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.DEVELOPER)
                        .count(1)
                        .build()))
                .executors(List.of(teamMember4))
                .build();
    }

    @Test
    void testCreateStage() {
        stageService.createStage(stageDto1);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage1);
    }

    @Test
    void deleteStageCascade() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage4);
        stageService.deleteStage(1L, SubtaskActionDto.CASCADE, null);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteAll(stage4.getTasks());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage4);
    }

    @Test
    void deleteStageClose() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage4);
        stageService.deleteStage(1L, SubtaskActionDto.CLOSE, null);
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
                new StageDto(1L, "stage", 1L, new ArrayList<>()));
        stage.setTasks(tasks);
        Stage stageNew = stageMapper.toEntity(
                new StageDto(2L, "Rtest", 1L, new ArrayList<>()));

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        Mockito.when(stageRepository.getById(2L)).thenReturn(stageNew);
        stageService.deleteStage(1L, SubtaskActionDto.MOVE_TO_NEXT_STAGE, 2L);

        stageNew.setTasks(tasks);

        Mockito.verify(stageRepository, Mockito.times(1)).save(stageNew);
        stage.setTasks(new ArrayList<>());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage);
    }

    @Test
    void testUpdateStageRolesThrowException() {
        stageGetById.setExecutors(List.of(teamMember4, teamMember1, teamMember2));
        Mockito.when(stageRepository.getById(1L)).thenReturn(stageGetById);
        assertThrows(DataValidationException.class, () -> stageService.updateStageRoles(1L, stageRolesDto));
    }

    @Test
    void testUpdateStageRoles() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stageGetById);
        Mockito.when(teamMemberJpaRepository.findByProjectId(1L)).thenReturn(List.of(teamMember1, teamMember2, teamMember3));
        Mockito.when(userContext.getUserId()).thenReturn(1L);

        stageService.updateStageRoles(1L, stageRolesDto);
        Mockito.verify(stageInvitationService, Mockito.times(1))
                .createStageInvitation(StageInvitation
                        .builder()
                        .description("Invitation to the stage " + stageGetById.getStageName())
                        .status(StageInvitationStatus.PENDING)
                        .stage(stageGetById)
                        .author(TeamMember.builder().id(userContext.getUserId()).build())
                        .invited(teamMember3)
                        .build());
    }

    @Test
    void testGetAllStages() {
        stageService.getAllStages();
        Mockito.verify(stageRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGetByStage() {
        stageService.getStageById(1L);
        Mockito.verify(stageRepository, Mockito.times(1)).getById(1L);
    }
}