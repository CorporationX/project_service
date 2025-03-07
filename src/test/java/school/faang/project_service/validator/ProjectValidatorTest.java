package school.faang.project_service.validator;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validations.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    private ProjectValidator projectValidator;
    private Project project;

    @BeforeEach
    public void setUp() {
        projectValidator = new ProjectValidator();
        project = new Project();
    }

    @Test
    public void testValidateIdNull() {
        assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateProjectIdNotNull(null));
    }

    @Test
    public void testValidateIdNotNull() {
        assertDoesNotThrow(() -> projectValidator.validateProjectIdNotNull(1L));
    }

    @Test
    public void testProjectNotExist() {
        assertThrows(DataValidationException.class,
                () -> projectValidator.doesProjectExist(Optional.empty()));
    }

    @Test
    public void testProjectExists() {
        assertDoesNotThrow(() -> projectValidator.doesProjectExist(Optional.of(project)));
    }

    @Test
    public void testProjectIsPublic() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        assertTrue(projectValidator.isPublicProject(project));
    }

    @Test
    public void testProjectIsNotPublic() {
        project.setVisibility(ProjectVisibility.PRIVATE);
        assertFalse(projectValidator.isPublicProject(project));
    }

    @Test
    void testParametersAreNull() {
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateAllParametersNotNull(null, null)
        );
    }

    @Test
    void testOnlyStatusNotNull() {
        assertDoesNotThrow(() ->
                projectValidator.validateAllParametersNotNull(ProjectStatus.IN_PROGRESS, null)
        );
    }

    @Test
    void testOnlyVisibilityNotNull() {
        assertDoesNotThrow(() ->
                projectValidator.validateAllParametersNotNull(null, ProjectVisibility.PUBLIC)
        );
    }

    @Test
    void testAllParametersNotNull() {
        assertDoesNotThrow(() ->
                projectValidator.validateAllParametersNotNull(ProjectStatus.CREATED, ProjectVisibility.PUBLIC)
        );
    }
}


