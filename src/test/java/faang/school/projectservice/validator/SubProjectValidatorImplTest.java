package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubProjectValidatorImplTest {

    @InjectMocks
    private SubProjectValidatorImpl validator;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("when different visibility exeption must be thrown")
    public void testValidateSubProjectVisibilityWithNonAppropriateValues() {
        ProjectVisibility parentProjectVisibility = ProjectVisibility.PUBLIC;
        ProjectVisibility childProjectVisibility = ProjectVisibility.PRIVATE;
        assertThrows(IllegalStateException.class,
                () -> validator.validateSubProjectVisibility(parentProjectVisibility, childProjectVisibility));
    }


    @Test
    public void validate_ShouldThrowCannotCreatePrivateProjectForPublicParent_WhenParentIsPublicAndProjectIsPrivate() {
        assertDoesNotThrow(() -> validator.validateSubProjectVisibility(ProjectVisibility.PUBLIC,ProjectVisibility.PUBLIC));
    }

}