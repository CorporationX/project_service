package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private StageMapper stageMapper;

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
        stage.setProject(project);

        Mockito.when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);
        Mockito.when(stageMapper.toDto(stage)).thenReturn(stageDto);

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
        stage.setProject(project);

        Mockito.when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);
        Mockito.when(stageMapper.toDto(stage)).thenReturn(stageDto);

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
        String errorMessage = "Project does not exist";
        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenThrow(new IllegalArgumentException());

        assertThrows(DataValidationException.class, () -> stageService.create(stageDto), errorMessage);
    }
}
