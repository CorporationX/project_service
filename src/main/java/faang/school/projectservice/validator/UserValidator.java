package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.internship.DataValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static faang.school.projectservice.exception.internship.InternshipError.NOT_FOUND_USER;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExistence(long userId) {
        try {
            log.debug("Fetching user with id {}", userId);
            UserDto userDto = userServiceClient.getUser(userId);
            log.info("Found user with id {}", userId);
        } catch (FeignException.NotFound e) {
            log.error("User with id {} not found", userId);
            throw new DataValidationException(NOT_FOUND_USER);
        }
    }
}
