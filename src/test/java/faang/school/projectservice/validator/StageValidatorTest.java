package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageValidatorTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private StageValidator stageValidator;

    StageDto stageDto;

    @BeforeEach
    void setUp() {
       stageDto = StageDto.builder().stageName("Stage").projectId(1L).stageRolesDto(new ArrayList<>()).build();
    }

    @Test
    void testValidateStage_thenStageNameNull_thenThrowDataValidationException() {
        // Arrange
        stageDto.setStageName(null);
        // Act & Assert
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }
    @Test
    void testValidateStage_thenProjectIdNull_thenThrowDataValidationException() {
        // Arrange
        stageDto.setProjectId(null);
        // Act & Assert
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }
    @Test
    void testValidateStage_thenStageRolesIsEmpty_thenThrowDataValidationException() {
        // Act & Assert
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateNullStageId_whenNull_thenThrowDataValidationException() {
        // Arrange
        Long stageId = null;
        // Act & Assert
        assertThrows(DataValidationException.class, () -> stageValidator.validateNullStageId(stageId));
    }

    @Test
    void testValidateStatusProject_whenStatusCancelled_thenThrowDataValidationException() {
        // Arrange
        Long id = 1L;
        Project project = Project.builder().id(1L).status(CANCELLED).build();
        when(projectRepository.getProjectById(id)).thenReturn(project);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> stageValidator.validateStatusProject(id));
    }
}