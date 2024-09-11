package faang.school.projectservice;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @InjectMocks
    private ProjectValidator projectValidator;
    @Test
    public void testValidateCancelledProject() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {projectValidator.validateProject(project);});
    }
    @Test
    public void testValidateProjectCompleted() {
        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {projectValidator.validateProject(project);});
    }
    @Test
    public void testValidateProject() {
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        assertDoesNotThrow(() -> projectValidator.validateProject(project));
    }
}
