package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.exception.PublishEventException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> implements EventPublisher<T> {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void publish(T event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(getTopic(), json);
        } catch (JsonProcessingException e) {
            String errorMessage = "Ошибка при сериализации ивента " + event.getClass() + " : " + e.getMessage();
            log.error(errorMessage);
            throw new PublishEventException(errorMessage, e);
        }
    }

    protected abstract String getTopic();
}
