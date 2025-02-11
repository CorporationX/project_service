package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
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
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageValidator stageValidator;

    private Project project;
    private Stage stage;
    private final Long PROJECT_ID = 1L;
    private final Long STAGE_ID = 2L;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        stage = Stage.builder()
                .stageId(STAGE_ID)
                .build();
    }

    @Test
    void validateStageCreation_ProjectNotFound_ThrowsException() {
        StageDto stageDto = new StageDto();
        stageDto.setProjectId(PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.validateStageCreation(stageDto));
    }

    @Test
    void validateStageCreation_ProjectCancelled_ThrowsBusinessException() {
        StageDto stageDto = new StageDto();
        stageDto.setProjectId(PROJECT_ID);
        project.setStatus(ProjectStatus.CANCELLED);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(BusinessException.class,
                () -> stageValidator.validateStageCreation(stageDto));
    }

    @Test
    void validateStageCreation_ValidProject_NoExceptionsThrown() {
        StageDto stageDto = new StageDto();
        stageDto.setProjectId(PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertDoesNotThrow(() -> stageValidator.validateStageCreation(stageDto));
    }

    @Test
    void getValidProject_ProjectNotFound_ThrowsException() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.getValidProject(PROJECT_ID));
    }

    @Test
    void getValidProject_ProjectExists_ReturnsProject() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        Project result = stageValidator.getValidProject(PROJECT_ID);
        assertEquals(project, result);
    }

    @Test
    void checkStageForUpdate_ProjectNotFound_ThrowsException() {
        StageUpdateDto updateDto = new StageUpdateDto();
        updateDto.setProjectId(PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.checkStageForUpdate(STAGE_ID, updateDto));
    }

    @Test
    void checkStageForUpdate_StageNotFoundInRepository_ThrowsException() {
        StageUpdateDto updateDto = new StageUpdateDto();
        updateDto.setProjectId(PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.checkStageForUpdate(STAGE_ID, updateDto));
    }

    @Test
    void checkStageForUpdate_StageNotInProject_ThrowsException() {
        StageUpdateDto updateDto = new StageUpdateDto();
        updateDto.setProjectId(PROJECT_ID);
        project.setStages(Collections.emptyList());

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.of(stage));

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.checkStageForUpdate(STAGE_ID, updateDto));
    }

    @Test
    void checkStageForUpdate_ValidData_NoExceptionsThrown() {
        StageUpdateDto updateDto = new StageUpdateDto();
        updateDto.setProjectId(PROJECT_ID);
        project.setStages(List.of(stage));

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.of(stage));

        assertDoesNotThrow(() -> stageValidator.checkStageForUpdate(STAGE_ID, updateDto));
    }

    @Test
    void checkStageToRemove_ProjectNotFound_ThrowsException() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.checkStageToRemove(PROJECT_ID, new StageDeleteDto()));
    }

    @Test
    void checkStageToRemove_NoStagesInProject_ThrowsException() {
        project.setStages(Collections.emptyList());

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(EntityNotFoundException.class,
                () -> stageValidator.checkStageToRemove(PROJECT_ID, new StageDeleteDto()));
    }

    @Test
    void checkStageToRemove_ValidData_NoExceptionsThrown() {
        project.setStages(List.of(stage));

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertDoesNotThrow(() -> stageValidator.checkStageToRemove(PROJECT_ID, new StageDeleteDto()));
    }
}