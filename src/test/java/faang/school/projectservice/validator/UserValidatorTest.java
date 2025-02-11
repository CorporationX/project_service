package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Meet;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private AuditorAwareImpl auditorAware;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
    }

    @Test
    void validateCurrentUserExists_ShouldThrowWhenNoAuthorizedUser() {
        // Given
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SecurityException.class, () -> userValidator.validateCurrentUserExists());
    }

    @Test
    void validateCurrentUserExists_ShouldThrowWhenUserNotExist() {
        // Given
        doThrow(FeignException.class).when(userServiceClient).getUser(1L);

        // When & Then
        assertThrows(DataValidationException.class, () -> userValidator.validateCurrentUserExists());
    }

    @Test
    void validateCurrentUserExists_ShouldNotThrowWhenUserExist() {
        // Given
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "", ""));

        // When & Then
        userValidator.validateCurrentUserExists();
    }

    @Test
    void validateUserIsMeetCreator_ShouldThrowWhenNoAuthorizedUser() {
        // Given
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());
        Meet meet = new Meet();

        // When & Then
        assertThrows(SecurityException.class, () -> userValidator.validateUserIsMeetCreator(meet));
    }

    @Test
    void validateUserIsMeetCreator_ShouldThrowWhenUserIsNotCreator() {
        // Given
        Meet meet = new Meet();
        meet.setCreatorId(2L);

        // When & Then
        assertThrows(DataValidationException.class, () -> userValidator.validateUserIsMeetCreator(meet));
    }

    @Test
    void validateUserIsMeetCreator_ShouldNotThrowWhenUserIsCreator() {
        // Given
        Meet meet = new Meet();
        meet.setCreatorId(1L);

        // When & Then
        userValidator.validateUserIsMeetCreator(meet);
    }
}
