package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SubProjectValidatorTest {
    @Mock
    private SubProjectService subProjectService;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private SubProjectValidator validatorClass;
    private Method validateOwnerId;
    private Method validateProjectId;
    private Method validateId;
    private Long rightId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;
        validatorClass = new SubProjectValidator(subProjectService, userServiceClient);
    }

    @Test
    public void testValidateProjectId() throws NoSuchMethodException {
        validateProjectId = validatorClass.getClass().getDeclaredMethod("validateProjectId", Long.class);
        validateProjectId.setAccessible(true);

        assertDoesNotThrow(() -> validateProjectId.invoke(validatorClass, rightId));

        try {
            validateProjectId.invoke(validatorClass, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }

        try {
            validateProjectId.invoke(validatorClass, 2 * rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
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