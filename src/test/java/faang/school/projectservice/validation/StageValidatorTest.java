package faang.school.projectservice.validation;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    void testGetStagesWithEmptyList() {
        project.setStages(new ArrayList<>());
        when(projectService.getProjectById(projectId)).thenReturn(project);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                stageValidator.getStages(projectId)
        );

        assertEquals("List is empty", exception.getMessage());
        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    void testValidationProjectByIdWithValidProject() {
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertDoesNotThrow(() -> stageValidator.validationProjectById(projectId));
        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    void testValidationProjectByIdWithNullProject() {
        when(projectService.getProjectById(projectId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stageValidator.validationProjectById(projectId)
        );

        assertEquals("Project not found", exception.getMessage());
        verify(projectService, times(1)).getProjectById(projectId);
    }

}