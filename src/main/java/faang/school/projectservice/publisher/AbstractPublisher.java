package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.exception.SerializeJsonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public abstract class AbstractPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String topicChannelName;

    protected void publishInTopic(T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new SerializeJsonException("Cannot serialize event to json");
        }
        ChannelTopic topic = new ChannelTopic(topicChannelName);
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
