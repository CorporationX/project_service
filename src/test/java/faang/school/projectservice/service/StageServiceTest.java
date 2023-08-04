package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.DeleteStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.filter.StageStatusFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    StageStatusFilter stageStatusFilter = new StageStatusFilter();
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private TaskRepository taskRepository;
    @Spy
    private StageMapper stageMapper;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private StageFilter stageFilter;
    @InjectMocks
    private StageService stageService;
    private Stage stage;
    private Stage stage1;
    private StageRolesDto stageRolesDto;

    private TeamMember teamMember1;
    private TeamMember teamMember2;
    private TeamMember teamMember3;
    private Stage stage5;
    private Stage stage2;

    @BeforeEach
    public void setUp() {

        stage5 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project
                        .builder()
                        .id(1L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.DEVELOPER)
                        .count(1)
                        .build()))
                .build();
        stage2 =  Stage
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
        List<Stage> stages1 = new ArrayList<>();
        stages1.add(stage5);
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
        stageRolesDto = StageRolesDto.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(2)
                .build();
    }

    @Test
    public void createStage_projectUnavailable() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CANCELLED)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        ProjectException projectException = assertThrows(ProjectException.class, () -> stageService.createStage(StageMapper.INSTANCE.toStageDto(stage)));
        verify(stageRepository, times(0)).save(stage);
        assertEquals("Project with id 1 unavailable", projectException.getMessage());
    }

    @Test
    public void createStage_validStage() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .build();
        StageException stageException = assertThrows(StageException.class, () -> stageService.createStage(StageMapper.INSTANCE.toStageDto(stage)));
        verify(stageRepository, times(0)).save(stage);
        assertEquals("Name cannot be empty", stageException.getMessage());
    }

    @Test
    public void createStage_correctAnswer() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        when(stageRepository.save(any())).thenReturn(stage);
        stageService.createStage(StageMapper.INSTANCE.toStageDto(stage));
        verify(stageRepository, times(1)).save(any());
    }

    @Test
    public void deleteStage_cascade() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(List.of(new Task(), new Task()))
                .build();
        when(stageRepository.getById(2L)).thenReturn(stage);
        stageService.deleteStage(2L, DeleteStageDto.CASCADE, null);
        verify(taskRepository, times(1)).deleteAll(stage.getTasks());
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    public void deleteStage_close() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(List.of(new Task(), new Task()))
                .build();
        when(stageRepository.getById(2L)).thenReturn(stage);
        stageService.deleteStage(2L, DeleteStageDto.CLOSE, null);
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    public void deleteStage_move() {
        List<Task> tasks = new ArrayList<>();
        Task task = Task.builder()
                .name("task")
                .build();
        tasks.add(task);

        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(tasks)
                .build();
        Stage stageNew = Stage.builder()
                .stageId(2L)
                .stageName("stage2")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(new ArrayList<>())
                .build();
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stageNew);
        stageService.deleteStage(1L, DeleteStageDto.MOVE_TO_ANOTHER_STAGE, 2L);

        stageNew.setTasks(tasks);

        verify(stageRepository, times(1)).save(stageNew);
        stage.setTasks(new ArrayList<>());
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    public void getAllStage_correctAnswer() {
        ProjectDto project = ProjectDto.builder()
                .id(5L)
                .name("project")
                .projectStatus(ProjectStatus.CANCELLED)
                .stageList(List.of(new StageDto(), new StageDto()))
                .build();
        List<StageDto> stageSize = stageService.getAllStage(project);
        assertEquals(2, stageSize.size());
    }

    @Test
    public void getAllStage_invalidStatus() {
        ProjectDto project = ProjectDto.builder()
                .id(5L)
                .name("project")
                .projectStatus(null)
                .stageList(List.of(new StageDto(), new StageDto()))
                .build();
        ProjectException projectException = assertThrows(ProjectException.class, () -> stageService.getAllStage(project));
        assertEquals("The project must have a status", projectException.getMessage());
    }

   @Test
    public void getStageById_correctAnswer() {
        StageDto stageDto = StageDto.builder()
                .stageId(9L)
                .stageName("stage2")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(new ArrayList<>())
                .stageRoles(new ArrayList<>())
                .build();
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);
        when(stageRepository.getById(9L)).thenReturn(stage);
        assertEquals("stage2", stageService.getStageById((9L)).getStageName());
    }

    @Test
    void testUpdateStageRolesNegative() {
        stage1 = Stage
                .builder()
                .stageRoles(List.of(StageRoles
                        .builder()
                        .teamRole(TeamRole.DEVELOPER)
                        .count(3)
                        .build()))
                .build();
        when(stageRepository.getById(1L)).thenReturn(stage1);
        assertThrows(RuntimeException.class, () -> stageService.updateStageRoles(1L, stageRolesDto));
    }

    @Test
    void testUpdateStageRoles() {
        when(stageRepository.getById(1L)).thenReturn(stage5);
        when(teamMemberJpaRepository.findByProjectId(1L)).thenReturn(List.of(teamMember1, teamMember2, teamMember3));
        stageRolesDto.setCount(2);

        stageService.updateStageRoles(1L, stageRolesDto);
        verify(teamMemberJpaRepository, times(1))
                .saveAll(List.of(teamMember1, teamMember2, teamMember3));
        stage5.getStageRoles().get(0).setCount(2);
        verify(stageRepository, times(1)).save(stage5);
    }
}