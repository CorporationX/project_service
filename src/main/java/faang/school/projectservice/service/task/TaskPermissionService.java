package faang.school.projectservice.service.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskPermissionService {

    private final UserServiceClient userServiceClient;
    private final AuditorAwareImpl auditorAware;

    public long validateTaskAccess() {
        long userId = getAuthenticatedUserId();
        validateUserExists(userId);
        return userId;
    }

    public void validateTaskUpdatePermission(Task task) throws AccessDeniedException {
        long userId = validateTaskAccess();
        if (!isReporterOrPerformer(task, userId)) {
            log.warn("User {} attempted to modify task {} without permissions", userId, task.getId());
            throw new AccessDeniedException("No task modification rights");
        }
    }

    public void validateTaskDeletePermission(Task task) throws AccessDeniedException {
        long userId = validateTaskAccess();
        if (!task.getReporterUserId().equals(userId)) {
            log.warn("User {} attempted to delete task {} without ownership", userId, task.getId());
            throw new AccessDeniedException("Only task creator can delete it");
        }
    }

    private long getAuthenticatedUserId() {
        return auditorAware.getCurrentAuditor()
                .orElseThrow(() -> {
                    log.warn("No active authorized user");
                    return new SecurityException("Authentication required");
                });
    }

    private void validateUserExists(long userId) {
        try {
            UserDto user = userServiceClient.getUser(userId);
            if (user == null) {
                throw new SecurityException("User not found");
            }
        } catch (Exception e) {
            log.error("User service error", e);
            throw new DataValidationException("User validation failed");
        }
    }

    private boolean isReporterOrPerformer(Task task, Long userId) {
        return task.getReporterUserId().equals(userId)
                || task.getPerformerUserId().equals(userId);
    }
}
