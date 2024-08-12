package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    private String calendarId;
    private Project project;
    private ProjectValidator projectValidator;

    @BeforeEach
    public void setUp(){
        calendarId = "calendarId";
        project = new Project();
        projectValidator = new ProjectValidator();
    }

    @Test
    @DisplayName("testing verifyProjectDoesNotHaveCalendar method with non-appropriate value")
    void testVerifyProjectDoesNotHaveCalendarWithNonAppropriateValue() {
        project.setCalendarId(calendarId);
        assertThrows(IllegalStateException.class, () -> projectValidator.verifyProjectDoesNotHaveCalendar(project));
    }

    @Test
    @DisplayName("testing verifyProjectDoesNotHaveCalendar method with appropriate value")
    void testVerifyProjectDoesNotHaveCalendarWithAppropriateValue() {
        assertDoesNotThrow(() -> projectValidator.verifyProjectDoesNotHaveCalendar(project));
    }

    @InjectMocks
    private ProjectValidator projectValidator;
    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        projectDto = ProjectDto.builder()
                .ownerId(1L)
                .name("Some name").build();
    }


    @Test
    @DisplayName("testing validateSubProjectVisibility with non appropriate values")
    public void testValidateSubProjectVisibilityWithNonAppropriateValues() {
        ProjectVisibility parentProjectVisibility = ProjectVisibility.PUBLIC;
        ProjectVisibility childProjectVisibility = ProjectVisibility.PRIVATE;
        assertThrows(IllegalStateException.class,
                () -> projectValidator.validateSubProjectVisibility(parentProjectVisibility, childProjectVisibility));
    }

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
