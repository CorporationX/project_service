package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.subproject.ProjectService;
import faang.school.projectservice.service.subproject.SubProjectService;
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
    private SubProjectService subProjectService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private SubProjectValidator validatorClass;
    private Method validateOwnerId;
    private Method validateParentProject;
    private Method validateStringData;
    private Method validateId;
    private Long rightId;
    private String str;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;
        str = "Ane string";
        validatorClass = new SubProjectValidator(projectService, subProjectService, userServiceClient);

        when(projectService.isExistProjectById(rightId)).thenReturn(false);
    }

    @Test
    public void testValidateOwnerId() throws NoSuchMethodException {
        validateOwnerId = validatorClass.getClass().getDeclaredMethod("validateOwnerId", Long.class);
        validateOwnerId.setAccessible(true);

        assertDoesNotThrow(() -> validateOwnerId.invoke(validatorClass, rightId));
        Mockito.verify(userServiceClient, Mockito.times(1))
                .getUser(rightId);

        try {
            validateOwnerId.invoke(validatorClass, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateParentProject() throws NoSuchMethodException {
        validateParentProject = validatorClass.getClass().getDeclaredMethod("validateParentProject", Long.class);
        validateParentProject.setAccessible(true);

        assertDoesNotThrow(() -> validateParentProject.invoke(validatorClass, rightId));
        Mockito.verify(projectService, Mockito.times(1))
                .getProjectById(rightId);

        try {
            validateParentProject.invoke(validatorClass, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateStringData() throws NoSuchMethodException {
        validateStringData = validatorClass.getClass().getDeclaredMethod("validateRequiredFields", String.class, String.class);
        validateStringData.setAccessible(true);

        assertDoesNotThrow(() -> validateStringData.invoke(validatorClass, List.of(str, str).toArray()));

        try {
            validateStringData.invoke(validatorClass, List.of("", str).toArray());
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateId() throws NoSuchMethodException {
        validateId = validatorClass.getClass().getDeclaredMethod("validateId", Long.class);
        validateId.setAccessible(true);

        assertDoesNotThrow(() -> validateId.invoke(validatorClass, rightId));

        try {
            validateId.invoke(validatorClass, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }
}