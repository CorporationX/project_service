package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Meet;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final AuditorAwareImpl auditorAware;
    private final UserServiceClient userServiceClient;

    public void validateCurrentUserExists() {
        long currentUser = validateCurrentAuditor();
        try {
            userServiceClient.getUser(currentUser);
        } catch (FeignException e) {
            log.warn("Meet creator not exists with id: {}", currentUser);
            throw new DataValidationException("Meet creator not exists with id: " + currentUser);
        }
    }

    public void validateUserIsMeetCreator(Meet meet) {
        long currentUser = validateCurrentAuditor();
        if (currentUser != meet.getCreatorId()) {
            throw new DataValidationException("Only creator can change his own meets. Meet id:" + meet.getId() +
                    ", actual creator id: " + meet.getCreatorId() + ", current user id: " + currentUser);
        }
    }

    private long validateCurrentAuditor() {
        return auditorAware.getCurrentAuditor()
                .orElseThrow(() -> {
                    log.warn("No active authorized user");
                    return new SecurityException("No active authorized user");
                });
    }
}
