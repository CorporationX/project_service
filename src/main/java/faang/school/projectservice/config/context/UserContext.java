package faang.school.projectservice.config.context;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserContext {
    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> userRoles = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in context");
        }
        return userId;
    }

    public Optional<Long> getUserIdOptional() {
        return Optional.ofNullable(userIdHolder.get());
    }

    public void setUserRoles(List<String> roles) {
        userRoles.set(roles);
    }

    public List<String> getUserRoles() {
        return userRoles.get();
    }

    public void clear() {
        userIdHolder.remove();
        userRoles.remove();
    }
}
