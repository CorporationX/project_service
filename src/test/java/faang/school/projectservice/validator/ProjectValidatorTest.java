package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    private static final long PROJECT_ID = 1L;
    private static final long USER_ID = 3L;

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectService projectService;

    @Test
    @DisplayName("Test check user permission when user has manager permission")
    void testCheckUserPermission() {

        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(true);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);

        assertDoesNotThrow(() -> projectValidator.checkUserPermission(PROJECT_ID, USER_ID));
    }

    @Test
    @DisplayName("Test check user permission when user has owner permission")
    void testCheckUserPermissionManagerPermission() {

        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.checkUserPermission(PROJECT_ID, USER_ID));
    }

    @Test
    @DisplayName("Test check user permission when user has no permission")
    void testCheckUserPermissionNoPermission() {

        when(projectService.checkManagerPermission(USER_ID, PROJECT_ID)).thenReturn(false);
        when(projectService.checkOwnerPermission(USER_ID, PROJECT_ID)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> projectValidator.checkUserPermission(PROJECT_ID, USER_ID));
    }
}
