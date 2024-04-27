package faang.school.projectservice.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public abstract class MessagePublisher <T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    public void publish(T event){
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
