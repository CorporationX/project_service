package faang.school.projectservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    private final ThreadLocal<Long> projectIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public Long getUserId() {
        return userIdHolder.get();
    }

    public void setProjectId(long projectId) {
        projectIdHolder.set(projectId);
    }

    public Long getProjectId() {
        return projectIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
