package faang.school.projectservice.service;

import faang.school.projectservice.dto.MethodDeletingStageDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilter;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilterImpl;
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
import faang.school.projectservice.repository.StageRepository;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    @Spy
    private StageMapperImpl stageMapper;

    private Stage stage1;
    private Stage stage2;
    private Stage stage3;

    private Stage stage4;

    private StageDto stageDto1;

    private StageRolesDto stageRolesDto;

    private TeamMember teamMember1;
    private TeamMember teamMember2;
    private TeamMember teamMember3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);
        Project project = Project
                .builder()
                .id(1L)
                .status(ProjectStatus.CREATED)
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
        stage2 = Stage
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
        stage3 = Stage
                .builder()
                .stageId(3L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
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
    }

    @Test
    void testCreateStage() {
        stageService.createStage(stageDto1);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage1);
    }

    @Test
    void testCreateStageNegativeProjectByCancelled() {
        stageDto1.getProject().setStatus(ProjectStatus.CANCELLED);
        assertThrows(DataValidationException.class, () -> stageService.createStage(stageDto1));
    }

    @Test
    void testGetStagesByProjectStatus() {
        Mockito.when(stageRepository.findAll()).thenReturn(List.of(stage1, stage2, stage3));
        List<ProjectStatusFilter> projectStatusFilters = List.of(
                new ProjectStatusFilterImpl()
        );
        ProjectStatusFilterDto filter = ProjectStatusFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();
        stageService = new StageService(stageRepository, taskRepository, stageMapper, projectStatusFilters, teamMemberJpaRepository);

        List<StageDto> actual = stageService.getStagesByProjectStatus(filter);
        List<StageDto> expected = Stream.of(stage1, stage3)
                .map(stageMapper::toDto)
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void deleteStageCascade() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage4);
        stageService.deleteStage(1L, MethodDeletingStageDto.CASCADE, null);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteAll(stage4.getTasks());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage4);
    }

    @Test
    void deleteStageClose() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage4);
        stageService.deleteStage(1L, MethodDeletingStageDto.CLOSE, null);
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
                new StageDto(1L, "stage", ProjectDto.builder().build(), new ArrayList<>()));
        stage.setTasks(tasks);
        Stage stageNew = stageMapper.toEntity(
                new StageDto(2L, "Rtest", ProjectDto.builder().build(), new ArrayList<>()));

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        Mockito.when(stageRepository.getById(2L)).thenReturn(stageNew);
        stageService.deleteStage(1L, MethodDeletingStageDto.MOVE_TO_NEXT_STAGE, 2L);

        stageNew.setTasks(tasks);

        Mockito.verify(stageRepository, Mockito.times(1)).save(stageNew);
        stage.setTasks(new ArrayList<>());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage);
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
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage1);
        assertThrows(DataValidationException.class, () -> stageService.updateStageRoles(1L, stageRolesDto));
    }

    @Test
    void testUpdateStageRoles() {
        Mockito.when(stageRepository.getById(1L)).thenReturn(stage1);
        Mockito.when(teamMemberJpaRepository.findByProjectId(1L)).thenReturn(List.of(teamMember1, teamMember2, teamMember3));
        stageRolesDto.setCount(2);

        stageService.updateStageRoles(1L, stageRolesDto);
        Mockito.verify(teamMemberJpaRepository, Mockito.times(1))
                .saveAll(List.of(teamMember1, teamMember2, teamMember3));
        stage1.getStageRoles().get(0).setCount(2);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage1);
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