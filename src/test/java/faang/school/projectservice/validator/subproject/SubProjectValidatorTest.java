package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubProjectValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private SubProjectValidator subProjectValidator;
    private Method validateOwnerId;
    private Method validateParentProject;
    private Method validateId;
    private Long rightId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;

        subProjectValidator = new SubProjectValidator(projectService, userServiceClient);

        when(projectService.isExistProjectById(rightId)).thenReturn(false);
    }

    @Test
    public void testValidateVisibility_Throw() {
        assertThrows(DataValidationException.class, () -> subProjectValidator.validateVisibility(ProjectVisibility.PUBLIC, ProjectVisibility.PRIVATE));
    }

    @Test
    public void testValidateSubProjectStatus_True() {
        Long withoutChildId = 10L;
        Long completedId = 11L;
        Long inProgressId = 12L;

        Project projectChildrenCompleted = Project.builder().status(ProjectStatus.COMPLETED).build();

        Project projectWithOutChildren = Project.builder().id(withoutChildId).status(ProjectStatus.COMPLETED).build();
        Project projectCompleted = Project.builder().id(completedId).status(ProjectStatus.COMPLETED).children(List.of(projectChildrenCompleted)).build();
        Project projectInProgress = Project.builder().id(inProgressId).status(ProjectStatus.IN_PROGRESS).build();

        Mockito.when(projectService.getProjectById(withoutChildId)).thenReturn(projectWithOutChildren);
        Mockito.when(projectService.getProjectById(completedId)).thenReturn(projectCompleted);
        Mockito.when(projectService.getProjectById(inProgressId)).thenReturn(projectInProgress);

        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectWithOutChildren.getId()));
        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectCompleted.getId()));
        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectInProgress.getId()));
    }

    @Test
    public void testValidateSubProjectStatus_Throw() {
        Long completedId = 11L;

        Project projectChildrenInProgress = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project projectCompleted = Project.builder().id(completedId).status(ProjectStatus.COMPLETED).children(List.of(projectChildrenInProgress)).build();

        Mockito.when(projectService.getProjectById(completedId)).thenReturn(projectCompleted);

        assertThrows(DataValidationException.class, () -> subProjectValidator.validateSubProjectStatus(projectCompleted.getId()));
    }

    @Test
    public void testValidateOwnerId() throws NoSuchMethodException {
        validateOwnerId = subProjectValidator.getClass().getDeclaredMethod("validateOwnerId", Long.class);
        validateOwnerId.setAccessible(true);

        assertDoesNotThrow(() -> validateOwnerId.invoke(subProjectValidator, rightId));
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(rightId);

        try {
            validateOwnerId.invoke(subProjectValidator, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateParentProject() throws NoSuchMethodException {
        validateParentProject = subProjectValidator.getClass().getDeclaredMethod("validateParentProject", Long.class);
        validateParentProject.setAccessible(true);

        assertDoesNotThrow(() -> validateParentProject.invoke(subProjectValidator, rightId));
        Mockito.verify(projectService, Mockito.times(1)).getProjectById(rightId);

        try {
            validateParentProject.invoke(subProjectValidator, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
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
}