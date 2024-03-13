package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<EventType> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    abstract void publish (EventType event);
    protected void convertAndSend(EventType event, String channelTopicName) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
            log.debug("converted event {} to json", event);
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException with event {}", event);
            throw new RuntimeException("Cannot serialize event to json");
        }
        redisTemplate.convertAndSend(channelTopicName, json);
        log.debug("json with event {} sent to topic {}", event, channelTopicName);
    }
}