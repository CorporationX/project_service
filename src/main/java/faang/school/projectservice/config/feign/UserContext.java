package faang.school.projectservice.config.feign;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new IllegalArgumentException("Отсутствует ID пользователя. Пожалуйста, убедитесь, что заголовок 'x-user-id' включен в запрос.");
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
