package faang.school.projectservice.integration.jira.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Управляет OAuth state в Redis для stateless архитектуры
 *
 * Используется для хранения state → userId mapping
 * между authorize и callback запросами
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthStateManager {
    private final RedisTemplate<String, Long> redisTemplate;

    private static final String STATE_KEY_PREFIX = "jira:oauth:state:";
    private static final Duration STATE_TTL = Duration.ofMinutes(10);

    /**
     * Сохранить state → userId mapping в Redis
     *
     * @param state случайный state token
     * @param userId ID пользователя
     */
    public void saveState(String state, Long userId) {
        String key = STATE_KEY_PREFIX + state;

        redisTemplate.opsForValue().set(key, userId, STATE_TTL);

        log.debug("Saved OAuth state in Redis: {} -> user {}, TTL: {} minutes",
                state, userId, STATE_TTL.toMinutes());
    }

    /**
     * Получить userId по state из Redis
     *
     * @param state state token из callback
     * @return userId или null если state не найден/истёк
     */
    public Long getUserIdByState(String state) {
        String key = STATE_KEY_PREFIX + state;

        Long userId = redisTemplate.opsForValue().get(key);

        if (userId == null) {
            log.warn("OAuth state not found or expired: {}", state);
        } else {
            log.debug("Retrieved userId {} for state: {}", userId, state);
        }

        return userId;
    }

    /**
     * Удалить использованный state из Redis
     * Вызывается после успешного callback
     *
     * @param state state token для удаления
     */
    public void removeState(String state) {
        String key = STATE_KEY_PREFIX + state;

        Boolean deleted = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(deleted)) {
            log.debug("Removed OAuth state from Redis: {}", state);
        } else {
            log.warn("OAuth state not found for deletion: {}", state);
        }
    }

    /**
     * Проверить существует ли state в Redis
     *
     * @param state state token
     * @return true если state существует и не истёк
     */
    public boolean isStateValid(String state) {
        String key = STATE_KEY_PREFIX + state;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
