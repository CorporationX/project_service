package faang.school.projectservice.validator;

import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class SubProjectValidatorImplTest {

    @InjectMocks
    private SubProjectValidatorImpl validator;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void validate_ShouldThrowParentProjectMustNotBeNull_WhenParentProjectIsNull() {
        Project project = Project.builder()
                .parentProject(null)
                .build();

        assertThrows(ParentProjectMusNotBeNull.class, () -> validator.validate(project));
    }


    @Test
    public void validate_ShouldThrowCannotCreatePrivateProjectForPublicParent_WhenParentIsPublicAndProjectIsPrivate() {
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(Project.builder().build())
                .build();

        Project project = Project.builder()
                .parentProject(parentProject)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        assertThrows(CannotCreatePrivateProjectForPublicParent.class, () -> validator.validate(project));
    }

    @Test
    public void validate_ShouldPass_WhenValidConditionsAreMet() {
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(Project.builder().build())
                .build();

        Project project = Project.builder()
                .parentProject(parentProject)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        validator.validate(project);
    }
}