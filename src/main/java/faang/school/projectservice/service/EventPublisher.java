package faang.school.projectservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class EventPublisher <T> implements MessagePublisher<T> {

    protected final RedisTemplate<String, Object> template;
    private final ObjectMapper mapper;
    private final String topic;

    @Override
    public void publish(T event) {
        String json = eventToJson(event);
        template.convertAndSend(topic, json);
    }
    private String eventToJson(T event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new DataValidationException("Failed to serialize event: " + e.getMessage());
        }
    }
}
