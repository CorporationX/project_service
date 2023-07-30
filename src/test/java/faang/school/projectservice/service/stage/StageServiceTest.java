package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    private StageMapperImpl stageMapper;

    private StageDto stageDto;

    @BeforeEach
    public void setUp() {
        stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto.setStageName("Name");
        stageDto.setProjectId(2L);
    }

    @Test
    public void testCreate_IfProjectStatusIsInProgress_PassesValidation() {
        Project project = new Project();
        project.setId(stageDto.getProjectId());
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(Project.builder().id(2L).build());

        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        StageDto createdStageDto = stageService.create(stageDto);

        assertNotNull(createdStageDto);
        assertEquals(stageDto, createdStageDto);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage);
    }

    @Test
    public void testCreate_IfProjectStatusIsCreated_PassesValidation() {
        Project project = new Project();
        project.setId(stageDto.getProjectId());
        project.setStatus(ProjectStatus.CREATED);

        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(Project.builder().id(2L).build());

        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        StageDto createdStageDto = stageService.create(stageDto);

        assertNotNull(createdStageDto);
        assertEquals(stageDto, createdStageDto);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage);
    }

    @Test
    public void testCreate_IfProjectStatusIsOnHold_ThrowsException() {
        Project project = new Project();
        project.setId(stageDto.getProjectId());
        project.setStatus(ProjectStatus.ON_HOLD);
        String errorMessage = String.format(
                "Project %d is %s", project.getId(), project.getStatus().name().toLowerCase());

        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.create(stageDto), errorMessage);
    }

    @Test
    public void testCreate_IfProjectStatusIsCancelled_ThrowsException() {
        Project project = new Project();
        project.setId(stageDto.getProjectId());
        project.setStatus(ProjectStatus.CANCELLED);
        String errorMessage = String.format(
                "Project %d is %s", project.getId(), project.getStatus().name().toLowerCase());

        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.create(stageDto), errorMessage);
    }

    @Test
    public void testCreate_IfProjectStatusIsCompleted_ThrowsException() {
        Project project = new Project();
        project.setId(stageDto.getProjectId());
        project.setStatus(ProjectStatus.COMPLETED);
        String errorMessage = String.format(
                "Project %d is %s", project.getId(), project.getStatus().name().toLowerCase());

        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.create(stageDto), errorMessage);
    }

    @Test
    public void testCreateInvalidStage_ProjectNotFound_ThrowsException() {
        String errorMessage = String.format("Project not found by id: %s", stageDto.getStageName());
        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenThrow(new EntityNotFoundException(errorMessage));

        assertThrows(EntityNotFoundException.class, () -> stageService.create(stageDto), errorMessage);
    }

    @Test
    public void testGetStageById() {
        Project project = new Project();
        project.setId(stageDto.getProjectId());

        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(project);

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        StageDto outputStageDto = stageService.getStageById(1L);
        assertEquals(stageDto, outputStageDto);
    }

    @Test
    public void testGetAllProjectStages() {
        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(Project.builder().id(2L).build());
        List<Stage> stages = List.of(stage);

        Project project = new Project();
        project.setId(stageDto.getProjectId());
        project.setStages(stages);

        List<StageDto> stageDtos = List.of(stageDto);

        Mockito.when(projectRepository.getProjectById(2L)).thenReturn(project);

        List<StageDto> output = stageService.getAllProjectStages(2L);

        assertEquals(stageDtos, output);
    }

    @Test
    public void testDeleteStageWithTasks() {
        stageDto.setTaskIds(List.of(1L));

        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(Project.builder().id(2L).build());
        stage.setTasks(List.of(Task.builder().id(1L).build()));

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        stageService.deleteStageWithTasks(1L);

        Mockito.verify(taskRepository, Mockito.times(1)).deleteAll(stage.getTasks());
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage);
    }

    @Test
    public void testDeleteStageCloseTasks() {
        stageDto.setTaskIds(List.of(1L));

        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(Project.builder().id(2L).build());
        stage.setTasks(List.of(Task.builder().id(1L).build()));

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        stageService.deleteStageCloseTasks(1L);

        Mockito.verify(taskRepository, Mockito.times(stage.getTasks().size())).save(Mockito.any(Task.class));
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage);
    }

    @Test
    public void testDeleteStageTransferTasks() {
        stageDto.setTaskIds(List.of(1L));

        StageDto stageToTransferDto = new StageDto();
        stageToTransferDto.setStageId(2L);

        Stage stage = new Stage();
        stage.setStageId(stageDto.getStageId());
        stage.setStageName(stageDto.getStageName());
        stage.setProject(Project.builder().id(2L).build());
        stage.setTasks(List.of(Task.builder().id(1L).build()));

        Stage stageToTransfer = new Stage();
        stageToTransfer.setStageId(stageToTransferDto.getStageId());

        Mockito.when(stageRepository.getById(1L)).thenReturn(stage);
        Mockito.when(stageRepository.getById(2L)).thenReturn(stageToTransfer);

        stageService.deleteStageTransferTasks(1L, 2L);

        Mockito.verify(stageRepository, Mockito.times(1)).save(stageToTransfer);
        Mockito.verify(stageRepository, Mockito.times(1)).delete(stage);
    }
}
