package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    protected void publish(ChannelTopic topic, T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.info("Cannot serialize event to json");
            throw new IllegalArgumentException("Cannot serialize event to json");
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}