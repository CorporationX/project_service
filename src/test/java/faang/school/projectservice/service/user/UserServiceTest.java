package faang.school.projectservice.service.user;

import faang.school.projectservice.client.feign.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.payment.UserClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDto mockUserDto;

    private final long testUserId = 123L;

    @Test
    public void userExists_ShouldReturnDto_WhenClientReturnsDto() {
        when(userServiceClient.getUser(testUserId))
                .thenReturn(new ResponseEntity<>(mockUserDto, HttpStatus.OK));

        UserDto userDto = userService.getUser(testUserId);

        assertEquals(mockUserDto, userDto);
        verify(userServiceClient).getUser(testUserId);
    }

    @Test
    public void userExists_ShouldThrowException_WhenNonOkStatus() {
        when(userServiceClient.getUser(testUserId))
                .thenReturn(new ResponseEntity<>(mockUserDto, HttpStatus.INTERNAL_SERVER_ERROR));

        UserClientException exception = assertThrows(UserClientException.class,
                () -> userService.getUser(testUserId));

        assertEquals("User client failed", exception.getMessage());
        verify(userServiceClient).getUser(testUserId);
    }
}
