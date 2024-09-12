package faang.school.projectservice.service.stage;

import faang.school.projectservice.controller.stage.StageWithTasksAction;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.stageinvitation.StageInvitationService;
import faang.school.projectservice.service.task.TaskService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.stage.StageServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    StageRepository stageRepository;
    @Mock
    TaskService taskService;
    @Mock
    ProjectService projectService;
    @Mock
    TeamMemberService teamMemberService;
    @Mock
    StageMapper stageMapper;
    @Mock
    StageInvitationService stageInvitationService;
    @Mock
    StageServiceValidator stageServiceValidator;
    @InjectMocks
    StageService stageService;
    StageDto stageDto;
    Stage stage;
    Stage stage2;
    Project project;
    ProjectDto projectDto;
    Team team;
    TeamMember teamMember;
    ProjectStatus projectStatus = ProjectStatus.IN_PROGRESS;

    @BeforeEach
    void setUp() {
        team = Team.builder().teamMembers(
                List.of(
                        TeamMember.builder().id(1L).userId(1L).roles(
                                List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                        ).build(),
                        TeamMember.builder().id(2L).userId(2L).roles(
                                List.of(TeamRole.DEVELOPER)
                        ).build()
                )
        ).build();
        teamMember = TeamMember.builder().id(1L).userId(1L).roles(
                List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
        ).build();
        stageDto = StageDto.builder()
                .stageName("name").projectId(1L)
                .tasks(List.of(1L, 2L))
                .stageRoles(List.of(
                        StageRolesDto.builder().teamRole(TeamRole.DEVELOPER.name()).count(2).build(),
                        StageRolesDto.builder().teamRole(TeamRole.MANAGER.name()).count(1).build()
                        ))
                .executors(List.of(
                        TeamMemberDto.builder().id(1L).userId(1L).roles(
                                List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                        ).build(),
                        TeamMemberDto.builder().id(2L).userId(2L).roles(
                                List.of(TeamRole.DEVELOPER)
                        ).build()
                ))
                .build();
        stage = Stage.builder()
                .stageName("name")
                .project(Project.builder().id(1L).build())
                .tasks(List.of(
                        Task.builder().id(1L).build(),
                        Task.builder().id(2L).build()
                ))
                .stageRoles(List.of(
                                StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(2).build(),
                                StageRoles.builder().teamRole(TeamRole.MANAGER).count(1).build()
                        ))
                .executors(List.of(
                                TeamMember.builder().id(1L).userId(1L).roles(
                                        List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                                ).build(),
                                TeamMember.builder().id(2L).userId(2L).roles(
                                        List.of(TeamRole.DEVELOPER)
                                ).build()
                ))
                .build();
        stage2 = Stage.builder()
                .stageName("name")
                .project(Project.builder().id(2L).build())
                .tasks(List.of(
                        Task.builder().id(3L).build(),
                        Task.builder().id(4L).build()
                ))
                .stageRoles(List.of(
                        StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(2).build(),
                        StageRoles.builder().teamRole(TeamRole.MANAGER).count(1).build()
                ))
                .executors(List.of(
                        TeamMember.builder().id(3L).userId(3L).roles(
                                List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                        ).build(),
                        TeamMember.builder().id(4L).userId(4L).roles(
                                List.of(TeamRole.DEVELOPER)
                        ).build()
                ))
                .build();
        project = Project.builder().id(1L)
                .stages(List.of(stage))
                .teams(List.of(team))
                .build();
        projectDto = ProjectDto.builder().id(1L)
                .build();
    }

    @Test
    void createStage() {
        prepareStageAndProject();
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);
        assertEquals(stageService.createStage(stageDto), stageDto);
    }

    @Test
    void getAllByStatus() {
        when(projectService.getAllProjectsByStatus(any(ProjectStatus.class))).thenReturn(List.of(project));
        when(stageMapper.toDto(any(Stage.class))).thenReturn(stageDto);
        assertEquals(stageService.getAllByStatus(projectStatus), List.of(stageDto));
    }

    @Test
    void handleCascade() {
        when(stageRepository.getById(any())).thenReturn(stage);
        stageService.handle(1L, StageWithTasksAction.CASCADE, null);
        verify(taskService, times(2)).delete(any());
    }

    @Test
    void handleClose() {
        when(stageRepository.getById(any())).thenReturn(stage);
        stageService.handle(1L, StageWithTasksAction.CLOSE, null);
        verify(taskService, times(2)).save(any());

        stage.getTasks().forEach(task -> assertEquals(TaskStatus.DONE, task.getStatus()));
    }

    @Test
    void handleTransfer() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stage2);
        stageService.handle(1L, StageWithTasksAction.TRANSFER, 2L);
        assertEquals(stage2.getTasks().size(), 4);
    }

    @Test
    void update() {
        prepareStageAndProject();
        when(stageRepository.save(any())).thenReturn(stage);
        assertEquals(stageService.update(1L, 1L, stageDto), stageDto);
    }

    @Test
    void getAll() {
        when(stageMapper.toDto(any(Stage.class))).thenReturn(stageDto);
        when(projectService.getProjectById(any())).thenReturn(projectDto);
        when(projectService.projectToEntity(any())).thenReturn(project);
        assertEquals(stageService.getAll(1L), List.of(stageDto));
    }

    @Test
    void getById() {
        when(stageMapper.toDto(any(Stage.class))).thenReturn(stageDto);
        when(stageRepository.getById(any())).thenReturn(stage);
        assertEquals(stageService.getById(1L), stageDto);
    }

    private void prepareStageAndProject() {
        when(stageMapper.toEntity(any(StageDto.class))).thenReturn(stage);
        when(projectService.getProjectById(any())).thenReturn(projectDto);
        when(projectService.projectToEntity(any())).thenReturn(project);
        when(stageMapper.toDto(any(Stage.class))).thenReturn(stageDto);
    }
}