package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    private Project childrenProject = Project.builder()
            .visibility(ProjectVisibility.PUBLIC)
            .status(ProjectStatus.IN_PROGRESS)
            .build();
    private Project parentProject = Project.builder()
            .id(parentProjectId)
            .visibility(ProjectVisibility.PUBLIC)
            .children(List.of(childrenProject))
            .build();
    private CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name("SubProject name")
            .description("SubProject description")
            .build();

    private  UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
            .status(ProjectStatus.IN_PROGRESS)
            .visibility(ProjectVisibility.PRIVATE)
            .build();

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

    @Test
    public void testValidateSubProjectForUpdateInvalidId() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateSubProjectForUpdate(0l, updateDto));
        assertEquals("project with this ID: " + 0 + " doesn't exist", exception.getMessage());
    }

    @Test
    public void testValidateSubProjectForUpdateInvalidStatus() {
        parentProject.setStatus(ProjectStatus.IN_PROGRESS);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateSubProjectForUpdate(parentProjectId, updateDto));
        assertEquals("Project " + parentProject.getId() + " already has status " + updateDto.getStatus(),
                exception.getMessage());
    }

    @Test
    public void testValidateSubProjectForUpdate() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setStatus(ProjectStatus.CREATED);

        validator.validateSubProjectForUpdate(parentProjectId, updateDto);

        assertEquals(ProjectVisibility.PRIVATE, childrenProject.getVisibility());
    }

    @Test
    public void testReadyToNewMomentNotClosedChildren() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.readyToNewMoment(parentProject, ProjectStatus.COMPLETED));
        assertEquals("Children projects status should be COMPLETED or CANCELLED",
                exception.getMessage());
    }

    @Test
    public void testReadyToNewMomentClosedChildren() {
        childrenProject.setStatus(ProjectStatus.COMPLETED);
        boolean result = validator.readyToNewMoment(parentProject, ProjectStatus.COMPLETED);

        assertEquals(true, result);
    }

    @Test
    public void testNotReadyToNewMoment() {
        boolean result = validator.readyToNewMoment(parentProject, ProjectStatus.IN_PROGRESS);

        assertEquals(false, result);
    }
}