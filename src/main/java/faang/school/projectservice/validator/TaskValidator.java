package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
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
    private final AuditorAwareImpl auditorAware;

    public long validateUserParticipationAndGetUserId() {
        long currentUserId = validateCurrentAuditor();

        if (currentUserId <= 0) {
            throw new IllegalArgumentException("User is not a valid participant");
        }
        try {
            UserDto user = userServiceClient.getUser(currentUserId);
            if (user == null) {
                throw new IllegalArgumentException("User not found or not a participant");
            }
        } catch (Exception e) {
            log.error("Error calling user-service", e);
            throw new IllegalArgumentException("User is not a participant", e);
        }
        return currentUserId;
    }

    private long validateCurrentAuditor() {
        return auditorAware.getCurrentAuditor()
                .orElseThrow(() -> {
                    log.warn("No active authorized user");
                    return new SecurityException("No active authorized user");
                });
    }
}
