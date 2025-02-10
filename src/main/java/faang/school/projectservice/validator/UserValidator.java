package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Meet;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final AuditorAwareImpl auditorAware;
    private final UserServiceClient userServiceClient;

    public void validateCurrentUserExists() {
        long currentUser = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new SecurityException("No active authorized user"));
        try {
            userServiceClient.getUser(currentUser);
        } catch (FeignException e) {
            throw new DataValidationException("Meet creator not exists with id: " + currentUser);
        }
    }

    public void validateUserIsMeetCreator(Meet meet) {
        long currentUser = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new SecurityException("No active authorized user"));
        if (currentUser != meet.getCreatorId()) {
            throw new DataValidationException("Only creator can change his own meets. Meet id:" + meet.getId() +
                    ", actual creator id: " + meet.getCreatorId() + ", current user id: " + currentUser);
        }
    }
}
