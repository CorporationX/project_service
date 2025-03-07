package school.faang.project_service.validator;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validations.validator.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class SubProjectValidatorTest {

    private SubProjectValidator subProjectValidator;
    private Project parentProject;

    @BeforeEach
    public void setUp() {
        subProjectValidator = new SubProjectValidator();
        parentProject = new Project();
    }

    @Test
    public void testInvalidForParent() {
        parentProject.setParentProject(new Project());
        parentProject.setStatus(ProjectStatus.COMPLETED);

        assertThrows(DataValidationException.class,
                () -> subProjectValidator.canBeParentProject(parentProject));
    }

    @Test
    void testValidForParent() {
        parentProject.setParentProject(null);
        parentProject.setStatus(ProjectStatus.CREATED);

        assertDoesNotThrow(() -> subProjectValidator.canBeParentProject(parentProject));
    }

    @Test
    public void testShouldBePublicFail() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        assertThrows(DataValidationException.class,
                () -> subProjectValidator.shouldBePublic(parentProject));
    }

    @Test
    public void testProjectIsPublic() {
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> subProjectValidator.shouldBePublic(parentProject));
    }

    @Test
    public void testChildNotCompleted() {
        Project childProject = new Project();
        childProject.setStatus(ProjectStatus.CREATED);
        List<Project> children = new ArrayList<>();
        children.add(childProject);

        assertThrows(DataValidationException.class,
                () -> subProjectValidator.childCompleted(children));
    }

    @Test
    public void testChildCompleted() {
        Project childProject1 = new Project();
        childProject1.setStatus(ProjectStatus.COMPLETED);

        Project childProject2 = new Project();
        childProject2.setStatus(ProjectStatus.CANCELLED);

        List<Project> children = List.of(childProject1, childProject2);

        assertDoesNotThrow(() -> subProjectValidator.childCompleted(children));
    }
}

