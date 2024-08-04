package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.ProjectService;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StageValidatorTest {
    @InjectMocks
    private StageValidator stageValidator;

    @Mock
    private ProjectService projectService;

    private Project project;
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        List<Stage> stages = new ArrayList<>();
        Stage stage = new Stage();
        stages.add(stage);

        project = new Project();
        project.setStages(stages);
    }

    @Test
    void testGetStagesWithNonEmptyList() {
        when(projectService.getProjectById(projectId)).thenReturn(project);

        List<Stage> result = stageValidator.getStages(projectId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetStagesWithEmptyList() {
        project.setStages(new ArrayList<>());
        when(projectService.getProjectById(projectId)).thenReturn(project);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                stageValidator.getStages(projectId)
        );

        assertEquals("List is empty", exception.getMessage());
    }

    @Test
    void testValidationProjectByIdWithValidProject() {
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertDoesNotThrow(() -> stageValidator.validationProjectById(projectId));
    }

    @Test
    void testValidationProjectByIdWithNullProject() {
        when(projectService.getProjectById(projectId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stageValidator.validationProjectById(projectId)
        );

        assertEquals("Project not found", exception.getMessage());
    }

}