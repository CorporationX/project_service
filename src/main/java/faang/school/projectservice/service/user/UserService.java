package faang.school.projectservice.service.user;

import faang.school.projectservice.client.feign.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.payment.UserClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserServiceClient userServiceClient;

    public UserDto getUser(long userId) {
        ResponseEntity<UserDto> response = userServiceClient.getUser(userId);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.error("User existence check failed for ID: {}. Response status: {}",
                    userId,
                    response.getStatusCode());
            throw new UserClientException("User client failed");
        }

        return response.getBody();
    }
}
