package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StageValidatorTest {

    @InjectMocks
    private StageValidator stageValidator;

    private StageDto stageDto;

    @BeforeEach
    void setUp() {
        stageDto = StageDto.builder()
                .project(ProjectDto.builder().id(1L).status(ProjectStatus.COMPLETED).build())
                .stageName("stageName")
                .stageRoles(List.of(StageRolesDto.builder().id(1L).build()))
                .build();
    }

    @Test
    void testValidateStageDtoIsNullThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(null));
    }

    @Test
    void testValidateStageDtoProjectIsNullThrowDataValidationException() {
        stageDto.setProject(null);
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateStageDtoStageNameIsNullThrowDataValidationException() {
        stageDto.setStageName(null);
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateStageDtoStageNameIsBlankThrowDataValidationException() {
        stageDto.setStageName("");
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateStageDtoStageRolesIsNullThrowDataValidationException() {
        stageDto.setStageRoles(null);
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateStageDtoStageRolesIsEmptyThrowDataValidationException() {
        stageDto.setStageRoles(Collections.emptyList());
        assertThrows(DataValidationException.class, () -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateStageDtoDoesNotThrowDataValidationException() {
        assertDoesNotThrow(() -> stageValidator.validateStage(stageDto));
    }

    @Test
    void testValidateStageDtoForProjectCompletedAndCancelledThrowDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> stageValidator.validateStageDtoForProjectCompletedAndCancelled(stageDto));
        stageDto.setProject(ProjectDto.builder().id(1L).status(ProjectStatus.CANCELLED).build());
        assertThrows(DataValidationException.class,
                () -> stageValidator.validateStageDtoForProjectCompletedAndCancelled(stageDto));
    }

    @Test
    void testValidateStageDtoForProjectCompletedAndCancelledDoesNotThrow() {
        stageDto.setProject(ProjectDto.builder().id(1L).status(ProjectStatus.CREATED).build());
        assertDoesNotThrow(() -> stageValidator.validateStageDtoForProjectCompletedAndCancelled(stageDto));
    }
}