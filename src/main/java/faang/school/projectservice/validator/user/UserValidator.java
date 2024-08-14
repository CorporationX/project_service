package faang.school.projectservice.validator.user;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    public final UserServiceClient userServiceClient;

    public void isUserActive(Long id) {
        UserDto user;

        try {
            user = userServiceClient.getUser(id);
        } catch (FeignException.NotFound e) {
            throw new DataValidationException(e.getMessage());
        }

        if (!user.getActive()) {
            throw new DataValidationException("User isn't active");
        }
    }
}
