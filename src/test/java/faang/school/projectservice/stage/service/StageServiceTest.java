package faang.school.projectservice.stage.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageDtoMapper;
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
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.StageServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class StageServiceTest {
    @Mock
    ProjectRepository projectRepository;
    @Mock
    StageRepository stageRepository;
    @Spy
    private StageDtoMapper stageDtoMapper = new faang.school.projectservice.mapper.StageDtoMapperImpl();
    @Mock
    StageInvitationRepository stageInvitationRepository;
    @InjectMocks
    StageServiceImpl stageService;

    private List<Stage> stages;
    private Project project;
    private Stage stage;
    private List<StageRoles> roles;
    private List<Task> tasks;
    private StageDto stageDto;

    @BeforeEach
    void setUp() {
        stages = List.of(
                Stage.builder().stageId(1L).stageName("First Stage").build(),
                Stage.builder().stageId(1L).stageName("Second Stage").build()
        );
        roles = List.of(
                StageRoles.builder().teamRole(TeamRole.DEVELOPER).id(1L).count(2).build(),
                StageRoles.builder().teamRole(TeamRole.DESIGNER).id(2L).count(1).build());
        tasks = List.of(
                Task.builder().status(TaskStatus.IN_PROGRESS).build(),
                Task.builder().status(TaskStatus.IN_PROGRESS).build()
        );
        project = Project.builder().status(ProjectStatus.IN_PROGRESS).id(1L).build();
        stage = Stage.builder().stageId(1L).tasks(tasks).stageRoles(roles).stageName("First Stage").project(project).build();
        stageDto = StageDto.builder().stageId(1L).tasks(tasks).stageName("First Stage").stageRoles(roles).project(project).build();
    }

    @Test
    void testFindById_WhenStageExists() {
        when(stageRepository.findById(1L)).thenReturn(Optional.ofNullable(stage));
        StageDto expectedDto = stageDtoMapper.toStageDto(stage);
        StageDto actualDto = stageService.findById(1L);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindById_WhenStageDoesNotExist() {
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stageService.findById(1L));
    }

    @Test
    void testFindAllStages_WhenProjectExists() {
        List<StageDto> expected = stageDtoMapper.toStageDtoList(stages);
        Project project = Project.builder().id(1L).status(ProjectStatus.IN_PROGRESS).stages(stages).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.ofNullable(project));
        Assertions.assertEquals(expected, stageService.findAllStages(1L));
    }

    @Test
    void testFindAllStages_WhenProjectDoesNotExist() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stageService.findAllStages(1L));
    }

    @Test
    void testFindAllStages_WhenProjectHasWrongStatus() {
        Project project = Project.builder().id(1L).status(ProjectStatus.ON_HOLD).stages(stages).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.ofNullable(project));
        assertThrows(IllegalArgumentException.class, () -> stageService.findAllStages(1L));
    }

    @Test
    void testUpdateStage_WhenValidData_UpdatesStage_NoInvitesSent() {
        System.out.println(stage);
        StageDto inputDto = StageDto.builder().stageId(1L).stageName("First Stage").project(project).stageRoles(roles).build();
        System.out.println(inputDto);
        when(stageRepository.findById(inputDto.getStageId())).thenReturn(Optional.ofNullable(stage));
        stageService.updateStage(inputDto);
        verify(stageRepository).save(any(Stage.class));
    }

    @Test
    void testUpdateStage_WhenStageNotFound_ThrowsException() {
        StageDto inputDto = StageDto.builder().stageId(999L).project(project).build();
        when(stageRepository.findById(inputDto.getStageId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stageService.updateStage(inputDto));
    }

    @Test
    void testUpdateStage_WhenProjectOnHold_ThrowsException() {
        project.setStatus(ProjectStatus.ON_HOLD);
        stageDto.setProject(project);
        assertThrows(IllegalArgumentException.class, () -> stageService.updateStage(stageDto));
    }

    @Test
    void testUpdateStage_WhenProjectCancelled_ThrowsException() {
        project.setStatus(ProjectStatus.CANCELLED);
        stageDto.setProject(project);
        assertThrows(IllegalArgumentException.class, () -> stageService.updateStage(stageDto));
    }

    @Test
    void testUpdateStage_WhenProjectCompleted_ThrowsException() {
        project.setStatus(ProjectStatus.COMPLETED);
        stageDto.setProject(project);
        assertThrows(IllegalArgumentException.class, () -> stageService.updateStage(stageDto));
    }

    @Test
    void testUpdateStage_WhenNewRolesAdded_SendsInvites() {

        StageDto inputDto = StageDto.builder()
                .stageId(1L)
                .project(project)
                .stageRoles(List.of(
                        StageRoles.builder().teamRole(TeamRole.DEVELOPER).id(1L).count(2).build(),
                        StageRoles.builder().teamRole(TeamRole.DESIGNER).id(2L).count(1).build(),
                        StageRoles.builder().teamRole(TeamRole.ANALYST).id(3L).count(4).build()))
                .build();

        TeamMember designer = TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build();
        Team team = Team.builder().teamMembers(List.of(designer)).build();
        project.setTeams(List.of(team));

        when(stageRepository.findById(inputDto.getStageId())).thenReturn(Optional.of(stage));
        when(stageInvitationRepository.existsByInvitedAndStage(any(), any())).thenReturn(false);

        stageService.updateStage(inputDto);

        verify(stageRepository).save(any());
        verify(stageInvitationRepository, atLeastOnce()).save(any());
    }

    @Test
    void testUpdateStage_WhenMembersAlreadyInvited_NoInvitesSent() {
        StageRoles role = StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(2).build();
        stage.setStageRoles(List.of(role));

        StageRoles increasedRole = StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(3).build();
        StageDto inputDto = StageDto.builder()
                .stageId(1L)
                .project(project)
                .stageRoles(List.of(increasedRole))
                .build();

        TeamMember developer = TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build();
        Team team = Team.builder().teamMembers(List.of(developer)).build();
        project.setTeams(List.of(team));

        when(stageRepository.findById(inputDto.getStageId())).thenReturn(Optional.of(stage));
        when(stageInvitationRepository.existsByInvitedAndStage(developer, stage)).thenReturn(true);

        stageService.updateStage(inputDto);

        verify(stageRepository).save(any());
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    void testDeleteStage_WhenNoStageFound_ThrowsException() {
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> stageService.findById(1L));
    }

    @Test
    void testDeleteStage_WhenDtoIsNull() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(Task.builder().id(1L).project(project).build());
        tasks.add(Task.builder().id(2L).project(project).build());
        stage.setTasks(tasks);
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        stageService.deleteStage(stage.getStageId(), null);
        boolean emptyTasks = stage.getTasks().isEmpty();
        Assertions.assertTrue(emptyTasks);
        verify(stageRepository, never()).save(any());
    }

    @Test
    void testDeleteStage_WhenInvalidProjectStatus_ThrowsException() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        Assertions.assertThrows(IllegalArgumentException.class, () -> stageService.deleteStage(stage.getStageId(), stageDto));
    }

    @Test
    void testDeleteStage_WhenTransferNeeded() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(Task.builder().id(1L).project(project).build());
        tasks.add(Task.builder().id(2L).project(project).build());
        stage.setTasks(tasks);
        stageDto.setTasks(new ArrayList<>());
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));
        stageService.deleteStage(stage.getStageId(), stageDto);
        Assertions.assertEquals(2, stageDto.getTasks().size());
    }

    @Test
    void testGetStagesWithFilters_WhenNoneMatch() {
        List<Task> tasks1 = List.of(Task.builder().status(TaskStatus.TESTING).build());
        List<StageRoles> roles1 = List.of(
                StageRoles.builder().teamRole(TeamRole.DEVELOPER).id(1L).count(2).build(),
                StageRoles.builder().teamRole(TeamRole.DESIGNER).id(2L).count(1).build());
        Project project1 = Project.builder().status(ProjectStatus.IN_PROGRESS).id(1L).build();
        Stage stage1 = Stage.builder().stageId(1L).stageRoles(roles1).stageName("First Stage").tasks(tasks1).project(project1).build();
        when(stageRepository.findAll()).thenReturn(List.of(stage1, stage));

        List<StageDto> result = stageService.getStagesWithFilters(TeamRole.ANALYST, TaskStatus.REVIEW);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    void testGetStagesWithFilters_WhenMatches() {
        List<Task> tasks1 = List.of(Task.builder().status(TaskStatus.TODO).build());
        List<StageRoles> roles1 = List.of(
                StageRoles.builder().teamRole(TeamRole.MANAGER).id(1L).count(2).build());
        Project project1 = Project.builder().status(ProjectStatus.IN_PROGRESS).id(1L).build();
        Stage stage1 = Stage.builder().stageId(1L).stageRoles(roles1).stageName("First Stage").tasks(tasks1).project(project1).build();

        List<Task> tasks2 = List.of(Task.builder().status(TaskStatus.IN_PROGRESS).build());
        List<StageRoles> roles2 = List.of(
                StageRoles.builder().teamRole(TeamRole.DESIGNER).id(1L).count(2).build());
        Project project2 = Project.builder().status(ProjectStatus.IN_PROGRESS).id(2L).build();
        Stage stage2 = Stage.builder().stageId(1L).stageRoles(roles2).stageName("First Stage").tasks(tasks2).project(project2).build();


        when(stageRepository.findAll()).thenReturn(List.of(stage1, stage2));

        System.out.println(stageDtoMapper.toStageDto(stage1));

        List<StageDto> result = stageService.getStagesWithFilters(TeamRole.DESIGNER, TaskStatus.IN_PROGRESS);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testSave() {
        stageService.save(stageDto);
        verify(stageRepository, atMostOnce()).save(any());
    }
}
