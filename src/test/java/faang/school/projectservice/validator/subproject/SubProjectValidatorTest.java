package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SubProjectValidatorTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private SubProjectValidator subProjectValidator;
    private Method validateProjectId;
    private Method validateStatus;
    private Method validateId;
    private Long rightId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;

        subProjectValidator = new SubProjectValidator(projectService);

        when(projectService.isExistProjectById(rightId)).thenReturn(false);
    }

    @Test
    public void testValidateProjectId() throws NoSuchMethodException {
        validateProjectId = subProjectValidator.getClass().getDeclaredMethod("validateProjectId", Long.class);
        validateProjectId.setAccessible(true);

        assertDoesNotThrow(() -> validateProjectId.invoke(subProjectValidator, rightId));

        try {
            validateProjectId.invoke(subProjectValidator, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }

        try {
            validateProjectId.invoke(subProjectValidator, 2 * rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateStatus() throws NoSuchMethodException {
        validateStatus = subProjectValidator.getClass().getDeclaredMethod("validateStatus", ProjectStatus.class);
        validateStatus.setAccessible(true);

        assertDoesNotThrow(() -> validateStatus.invoke(subProjectValidator, ProjectStatus.CREATED));
    }

    @Test
    public void testValidateId() throws NoSuchMethodException {
        validateId = subProjectValidator.getClass().getDeclaredMethod("validateId", Long.class);
        validateId.setAccessible(true);

        assertDoesNotThrow(() -> validateId.invoke(subProjectValidator, rightId));

        try {
            validateId.invoke(subProjectValidator, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateSubProjectStatus_True() {
        Long withoutChildId = 10L;
        Long completedId = 11L;
        Long inProgressId = 12L;

        Project projectChildrenCompleted = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        Project projectWithOutChildren = Project.builder()
                .id(withoutChildId)
                .status(ProjectStatus.COMPLETED)
                .build();
        Project projectCompleted = Project.builder()
                .id(completedId)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(projectChildrenCompleted))
                .build();
        Project projectInProgress = Project.builder()
                .id(inProgressId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Mockito.when(projectService.getProjectById(withoutChildId))
                .thenReturn(projectWithOutChildren);
        Mockito.when(projectService.getProjectById(completedId))
                .thenReturn(projectCompleted);
        Mockito.when(projectService.getProjectById(inProgressId))
                .thenReturn(projectInProgress);

        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectWithOutChildren.getId()));
        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectCompleted.getId()));
        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectInProgress.getId()));
    }

    @Test
    public void testValidateSubProjectStatus_Throw() {
        Long completedId = 11L;

        Project projectChildrenInProgress = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project projectCompleted = Project.builder()
                .id(completedId)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(projectChildrenInProgress))
                .build();

        Mockito.when(projectService.getProjectById(completedId))
                .thenReturn(projectCompleted);

        assertThrows(DataValidationException.class,
                () -> subProjectValidator.validateSubProjectStatus(projectCompleted.getId()));
    }

}