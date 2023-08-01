package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.mapper.stage.StageRolesMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    @Spy
    private StageMapperImpl stageMapper;
    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    private StageDto stageDto;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);

        StageRolesDto stageRolesDto = StageRolesDto.builder()
                .teamRole(TeamRole.DEVELOPER)
                .count(1)
                .build();

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("Name")
                .projectId(2L)
                .stageRoles(List.of(stageRolesDto))
                .build();
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
        stage.setStageRoles(List.of(StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(1).build()));

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
        stage.setStageRoles(List.of(StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(1).build()));

        Mockito.when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        StageDto createdStageDto = stageService.create(stageDto);

        assertNotNull(createdStageDto);
        assertEquals(stageDto, createdStageDto);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage);
    }

    @Deprecated
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
}
