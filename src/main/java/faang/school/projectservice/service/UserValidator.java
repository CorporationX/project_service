package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserServiceClient userServiceClient;

    public void validateUser(@Valid @NotNull Long userId) {
        userServiceClient.getUser(userId);
    }

    public void validateUsers(@Valid @NotNull List<Long> userIds) {
        userServiceClient.getUsersByIds(userIds);
    }
}
