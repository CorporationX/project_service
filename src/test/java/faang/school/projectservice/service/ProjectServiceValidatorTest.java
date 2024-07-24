package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceValidatorTest {
    @InjectMocks
    private ProjectServiceValidator validator;
    @Mock
    private ProjectRepository repository;
    @Mock
    private UserContext userContext;
    private Long parentProjectId = 1L;
    private Long ownerId = 100L;
    private Project parentProject = Project.builder()
            .id(parentProjectId)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    private CreateSubProjectDto subProjectDto = new CreateSubProjectDto();

    {
        subProjectDto.setParentProjectId(parentProjectId);
        subProjectDto.setName("SubProject name");
        subProjectDto.setDescription("SubProject description");
        subProjectDto.setVisibility(null);
    }

    @BeforeEach
    void setUp() {
        lenient().when(userContext.getUserId()).thenReturn(ownerId);
        lenient().when(repository.existsById(parentProjectId)).thenReturn(true);
        lenient().when(repository.getProjectById(parentProjectId)).thenReturn(parentProject);
    }

    @Test
    public void testValidateSubProjectWithIncorrectParent() {
        when(repository.existsById(parentProjectId)).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.getParentAfterValidateSubProject(subProjectDto));
        String expectedMessage = "project with this ID: 1 doesn't exist";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testValidateSubProjectWithIncorrectVisibility() {
        subProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.getParentAfterValidateSubProject(subProjectDto));
        String expectedMessage = "Visibility of parent project and subproject should be equals";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testValidateSubProjectExistsByOwnerUserIdAndName() {
        when(repository.existsByOwnerUserIdAndName(ownerId, subProjectDto.getName())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.getParentAfterValidateSubProject(subProjectDto));

        String exceptedMessage = "User with ID " + ownerId
                + " already has project with name " + subProjectDto.getName();

        assertEquals(exceptedMessage, exception.getMessage());
    }

    @Test
    public void testValidateSubProject() {
        Project result = validator.getParentAfterValidateSubProject(subProjectDto);

        assertEquals(parentProject, result);
    }
}