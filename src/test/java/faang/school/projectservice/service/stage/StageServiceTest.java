package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.ProjectStatusException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.mapper.stage.StageRolesMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Spy
    @InjectMocks
    private StageMapperImpl stageMapper = new StageMapperImpl();

    @Spy
    StageRolesMapperImpl stageRolesMapper = new StageRolesMapperImpl();

    private StageDto stageDto;
    private Stage stage;
    private final long stageId = 1;

    @BeforeEach
    void setUp() {
        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("Some stage")
                .projectId(1L)
                .stageRoles(List.of(StageRolesDto
                        .builder()
                        .teamRole(TeamRole.DEVELOPER)
                        .count(1)
                        .build()))
                .build();

        stage = stageMapper.toEntity(stageDto);
    }

    @Test
    void testCreateWhenProjectStatusCANCELLED() {
        when(projectRepository.getProjectById(1L)).thenReturn(getProjectByStatus(ProjectStatus.CANCELLED));

        assertThrows(ProjectStatusException.class, () -> stageService.create(stageDto));
    }

    @Test
    void TestCreateWhenProjectStatusCOMPLETED() {
        when(projectRepository.getProjectById(1L)).thenReturn(getProjectByStatus(ProjectStatus.COMPLETED));

        assertThrows(ProjectStatusException.class, () -> stageService.create(stageDto));
    }

    private Project getProjectByStatus(ProjectStatus status) {
        return Project.builder()
                .status(status)
                .build();
    }

    @Test
    void testCreateWhenSuccess() {
        when(projectRepository.getProjectById(1L)).thenReturn(getProjectByStatus(ProjectStatus.IN_PROGRESS));
        when(stageRepository.save(stage)).thenReturn(stage);

        StageDto returnedDto = stageService.create(stageDto);

        verify(projectRepository, times(1)).getProjectById(1L);
        verify(stageRepository, times(1)).save(stage);
        assertEquals(stageDto, returnedDto);
    }

    @Test
    void testGetByStatus() {
        List<Stage> stages = List.of(stage);
        List<StageDto> expectedStagesDto = stages.stream()
                .map(stageMapper::toDto)
                .toList();

        when(stageRepository.findByStatus(ProjectStatus.IN_PROGRESS)).thenReturn(stages);

        List<StageDto> returnedStagesDto = stageService.getByStatus(ProjectStatus.IN_PROGRESS);

        verify(stageRepository, times(1)).findByStatus(ProjectStatus.IN_PROGRESS);
        assertEquals(expectedStagesDto, returnedStagesDto);
    }

    @Test
    void testDeleteStageById() {
        stage.setTasks(List.of(Task.builder().build()));
        List<Task> tasks = stage.getTasks();
        StageDto expectedStageDto = stageMapper.toDto(stage);

        when(stageRepository.getById(stageId)).thenReturn(stage);
        doNothing().when(taskRepository).deleteAll(tasks);
        doNothing().when(stageRepository).delete(stage);

        StageDto returnedStageDto = stageService.deleteStageById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        verify(taskRepository, times(1)).deleteAll(tasks);
        verify(stageRepository, times(1)).delete(stage);
        assertEquals(expectedStageDto, returnedStageDto);
    }

    @Test
    void deleteStageWithClosingTasks() {
        stage.setTasks(List.of(Task.builder().build()));
        List<Task> tasks = stage.getTasks();
        StageDto expectedStageDto = stageMapper.toDto(stage);

        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(taskRepository.saveAll(tasks)).thenReturn(tasks);
        doNothing().when(stageRepository).delete(stage);

        StageDto returnedStageDto = stageService.deleteStageWithClosingTasks(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        verify(taskRepository, times(1)).saveAll(tasks);
        verify(stageRepository, times(1)).delete(stage);
        assertEquals(expectedStageDto, returnedStageDto);
    }

    @Test
    void testUpdateWhenTeamMembersNotEmpty() {
        prepareStage(TeamRole.DEVELOPER);
        prepareMocks(TeamRole.DEVELOPER);

        StageDto returnedStageDto = stageService.update(stageId, stageDto);

        verifyMocks();
        assertEquals(stageDto, returnedStageDto);
    }

    @Test
    void testUpdateWhenTeamMembersEmpty() {
        prepareStage(TeamRole.DEVELOPER);
        prepareMocks(TeamRole.DESIGNER);

        StageDto returnedStageDto = stageService.update(stageId, stageDto);

        verifyMocks();
        assertEquals(stageDto, returnedStageDto);
    }

    private void prepareStage(TeamRole teamRole) {
        stage.setProject(Project.builder().id(1L).build());
        stage.setExecutors(List.of(TeamMember
                .builder()
                .roles(List.of(teamRole))
                .build()));
    }

    private void prepareMocks(TeamRole teamRole) {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(projectRepository.getProjectById(1L)).thenReturn(getProjectByTeamRole(teamRole));
        when(stageRepository.save(any())).thenReturn(stage);
    }

    private void verifyMocks() {
        verify(stageRepository, times(1)).getById(stageId);
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(stageRepository, times(1)).save(any(Stage.class));
    }

    private Project getProjectByTeamRole(TeamRole teamRole) {
        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();

        teamMember.setRoles(List.of(teamRole));
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        return project;
    }

    @Test
    void testGetAll() {
        List<Stage> stages = List.of(stage);
        List<StageDto> expectedStagesDto = List.of(stageDto);

        when(stageRepository.findAll()).thenReturn(stages);

        List<StageDto> returnedStagesDto = stageService.getAll();

        verify(stageRepository, times(1)).findAll();
        assertEquals(expectedStagesDto, returnedStagesDto);
    }

    @Test
    void testGetById() {
        when(stageRepository.getById(stageId)).thenReturn(stage);

        StageDto returnedStageDto = stageService.getById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        assertEquals(stageDto, returnedStageDto);
    }
}