package faang.school.projectservice.subproject.validator;

import faang.school.projectservice.model.entity.ProjectVisibility;
import faang.school.projectservice.validator.subproject.SubProjectValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

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