package faang.school.projectservice.validator;

import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectValidatorTest {

    private ProjectValidator projectValidator;

    @BeforeEach
    public void setUp() {
        projectValidator = new ProjectValidator();
    }

    @Test
    @DisplayName("testing validateSubProjectVisibility with non appropriate values")
    public void testValidateSubProjectVisibilityWithNonAppropriateValues() {
        ProjectVisibility parentProjectVisibility = ProjectVisibility.PUBLIC;
        ProjectVisibility childProjectVisibility = ProjectVisibility.PRIVATE;
        assertThrows(IllegalStateException.class,
                () -> projectValidator.validateSubProjectVisibility(parentProjectVisibility, childProjectVisibility));
    }
}
@InjectMocks
    private ProjectValidator projectValidator;
    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto = ProjectDto.builder()
            .ownerId(1L)
            .name("Some name").build();

    @Test
    void validateProjectByOwnerWithNameOfProjectTest() {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        projectValidator.validateProjectByOwnerWithNameOfProject(projectDto);
        verify(projectRepository).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
    }

    @Test
    void validateProjectWithNameOfProjectNotValidNameTest() {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> projectValidator.validateProjectByOwnerWithNameOfProject(projectDto));
        verify(projectRepository).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
    }
}
