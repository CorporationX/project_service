package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class MessagePublisher<T> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;

    public void publish(T t) {
        String message;
        try {
            message = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.info("Method: publish {}", e.getMessage());
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(getEventTopic().getTopic(), message);
        log.info("Published message: {} to channel: {}", message, getEventTopic().getTopic());
    }

    protected abstract ChannelTopic getEventTopic();
}
