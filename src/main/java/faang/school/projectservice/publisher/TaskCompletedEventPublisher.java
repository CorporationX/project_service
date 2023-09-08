package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.redis.TaskCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskCompletedEventPublisher {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${spring.data.redis.channels.task_channel.name}")
    private String channel;
    private final ObjectMapper objectMapper;

    public void publishMessage(TaskCompletedEvent event) {
        String jsonEvent;
        try {
            jsonEvent = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(channel, jsonEvent);
        log.info("Message published to channel " + channel);
    }
}
