package school.faang.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.validator.TaskValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AuditorAwareImpl auditorAware;

    @InjectMocks
    private TaskValidator taskValidator;

    private final long validUserId = 1L;

    @Test
    void testValidateUserParticipationAndGetUserIdWithValidUser_ReturnsUserId() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(validUserId));
        when(userServiceClient.getUser(validUserId)).thenReturn(new UserDto(validUserId, "user", "user@example.com"));

        long result = taskValidator.validateUserParticipationAndGetUserId();

        assertEquals(validUserId, result);
        verify(auditorAware).getCurrentAuditor();
        verify(userServiceClient).getUser(validUserId);
    }

    @Test
    void testValidateUserParticipationAndGetUserIdWithInvalidUserId_ThrowsException() {
        long invalidUserId = -1L;
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(invalidUserId));

        assertThrows(IllegalArgumentException.class, () -> taskValidator.validateUserParticipationAndGetUserId());
    }

    @Test
    void testValidateUserParticipationAndGetUserId_UserNotFound_ThrowsException() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(validUserId));
        when(userServiceClient.getUser(validUserId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskValidator.validateUserParticipationAndGetUserId());
    }

    @Test
    void testValidateUserParticipationAndGetUserId_NoCurrentAuditor_ThrowsException() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> taskValidator.validateUserParticipationAndGetUserId());
    }
}
