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
    Project parentProject;
    Project project;
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
        createParentProject(ProjectVisibility.PUBLIC,ProjectVisibility.PRIVATE);

        assertThrows(CannotCreatePrivateProjectForPublicParent.class, () -> validator.validate(project));
    }

    @Test
    public void validate_ShouldPass_WhenValidConditionsAreMet() {
        createParentProject(ProjectVisibility.PUBLIC,ProjectVisibility.PUBLIC);

        validator.validate(project);
    }

    private void createParentProject(ProjectVisibility parentVisibility ,ProjectVisibility visibility) {
         parentProject = Project.builder()
                .visibility(parentVisibility)
                .parentProject(Project.builder().build())
                .build();

         project = Project.builder()
                .parentProject(parentProject)
                .visibility(visibility)
                .build();
    }
}