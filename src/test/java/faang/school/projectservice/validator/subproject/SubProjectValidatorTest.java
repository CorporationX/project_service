package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SubProjectValidatorTest {
    @Mock
    private SubProjectService subProjectService;
    @InjectMocks
    private SubProjectValidator validatorClass;
    private Method validateProjectId;
    private Method validateStatus;
    private Method validateId;
    private Long rightId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;
        validatorClass = new SubProjectValidator(subProjectService);

        when(subProjectService.isExistProjectById(rightId)).thenReturn(false);
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
    public void testValidateStatus() throws NoSuchMethodException {
        validateStatus = validatorClass.getClass().getDeclaredMethod("validateStatus", ProjectStatus.class);
        validateStatus.setAccessible(true);

        assertDoesNotThrow(() -> validateStatus.invoke(validatorClass, ProjectStatus.CREATED));
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