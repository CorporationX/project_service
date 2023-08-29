package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class StageValidatorTest {
    @InjectMocks
    private StageValidator stageValidator;
    @Mock
    private Stage stage;
    @Mock
    private Project project;

    @Test
    public void testIsCompletedOrCancelled_StageCancelled() {
        when(stage.getStatus()).thenReturn(StageStatus.CANCELLED);

        assertThrows(DataValidationException.class, () ->
                stageValidator.isCompletedOrCancelled(stage));
    }

    @Test
    public void testIsCompletedOrCancelled_StageCompleted() {
        when(stage.getStatus()).thenReturn(StageStatus.COMPLETED);


        assertThrows(DataValidationException.class, () ->
                stageValidator.isCompletedOrCancelled(stage));
    }

    @Test
    public void testIsCompletedOrCancelled_ProjectCancelled() {
        when(stage.getStatus()).thenReturn(StageStatus.CREATED);
        when(stage.getProject()).thenReturn(project);
        when(project.getStatus()).thenReturn(ProjectStatus.CANCELLED);

        assertThrows(DataValidationException.class, () ->
                stageValidator.isCompletedOrCancelled(stage));
    }

    @Test
    public void testIsCompletedOrCancelled_ProjectCompleted() {
        when(stage.getStatus()).thenReturn(StageStatus.IN_PROGRESS);
        when(stage.getProject()).thenReturn(project);
        when(project.getStatus()).thenReturn(ProjectStatus.COMPLETED);

        assertThrows(DataValidationException.class, () ->
                stageValidator.isCompletedOrCancelled(stage));
    }

    @Test
    public void testIsCompletedOrCancelled_StageAndProjectActive() {
        when(stage.getStatus()).thenReturn(StageStatus.CREATED);
        when(stage.getProject()).thenReturn(project);
        when(project.getStatus()).thenReturn(ProjectStatus.ON_HOLD);

        assertDoesNotThrow(() ->
                stageValidator.isCompletedOrCancelled(stage));
    }
}