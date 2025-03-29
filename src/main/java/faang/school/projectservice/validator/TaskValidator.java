package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskValidator {

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public long validateUserParticipationAndGetUserId() {
        long currentUserId = userContext.getUserId();
        log.info("Validating user participation for user ID: {}", currentUserId);

        if (currentUserId <= 0) {
            throw new IllegalArgumentException("User is not a valid participant");
        }
        try {
            log.info("Calling userServiceClient.getUser({})", currentUserId);
            UserDto user = userServiceClient.getUser(currentUserId);
            log.info("Received user: {}", user);
            if (user == null) {
                throw new IllegalArgumentException("User not found or not a participant");
            }
        } catch (Exception e) {
            log.error("Error calling user-service", e);
            throw new IllegalArgumentException("User is not a participant", e);
        }
        return currentUserId;
    }
}
