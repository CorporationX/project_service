package faang.school.projectservice.service.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskPermissionServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AuditorAwareImpl auditorAware;

    @InjectMocks
    private TaskPermissionService taskPermissionService;

    private Task task;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .reporterUserId(1L)
                .performerUserId(2L)
                .build();

        userDto = new UserDto(1L, "test user", "test@example.com");
    }

    @Test
    void testValidateTaskAccessSuccess() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        long result = taskPermissionService.validateTaskAccess();

        assertEquals(1L, result);
        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(1L);
    }

    @Test
    void testValidateTaskAccessNoUser() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> taskPermissionService.validateTaskAccess());
        verify(auditorAware, times(1)).getCurrentAuditor();
    }

    @Test
    void testValidateTaskAccessUserNotFound() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(userServiceClient.getUser(1L)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> taskPermissionService.validateTaskAccess());
        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(1L);
    }

    @Test
    void testValidateTaskAccessServiceError() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(userServiceClient.getUser(1L)).thenThrow(new RuntimeException());

        assertThrows(DataValidationException.class, () -> taskPermissionService.validateTaskAccess());
        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(1L);
    }

    @Test
    void testValidateTaskUpdatePermissionSuccess() throws Exception {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        taskPermissionService.validateTaskUpdatePermission(task);

        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(1L);
    }

    @Test
    void testValidateTaskUpdatePermissionAccessDenied() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(3L));
        when(userServiceClient.getUser(3L)).thenReturn(userDto);

        assertThrows(AccessDeniedException.class, () ->
                taskPermissionService.validateTaskUpdatePermission(task));
        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(3L);
    }

    @Test
    void testValidateTaskDeletePermissionSuccess() throws Exception {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        taskPermissionService.validateTaskDeletePermission(task);

        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(1L);
    }

    @Test
    void testValidateTaskDeletePermission_AccessDenied() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(2L));
        when(userServiceClient.getUser(2L)).thenReturn(userDto);

        assertThrows(AccessDeniedException.class, () ->
                taskPermissionService.validateTaskDeletePermission(task));
        verify(auditorAware, times(1)).getCurrentAuditor();
        verify(userServiceClient, times(1)).getUser(2L);
    }
}