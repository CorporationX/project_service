package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractMessagePublisher<T> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    @Async
    public void publish(T event) {
        String message = getStringMessage(event);
        publishToRedis(message);
    }

    @Retryable(
        retryFor = RuntimeException.class,
        backoff = @Backoff(delayExpression = "{spring.data.redis.publisher.delay}")
    )
    private void publishToRedis(String message) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), message);
            log.info("Message was sent: {} into {}", message, topic.getTopic());
        } catch (Exception e) {
            log.error("Message wasn't sent: {} into {}", message, topic.getTopic(), e);
            throw new RuntimeException(e);
        }
    }

    private String getStringMessage(T event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Message converted: {}", message);
            return message;
        } catch (JsonProcessingException e) {
            log.error("Parsing error: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
