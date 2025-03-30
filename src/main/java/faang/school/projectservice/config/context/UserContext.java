package faang.school.projectservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        long id = 0;
        try {
            id = userIdHolder.get();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
