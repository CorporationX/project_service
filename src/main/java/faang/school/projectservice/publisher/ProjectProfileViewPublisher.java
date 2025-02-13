package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectProfileViewPublisher {

    private final static String PROJECT_PROFILE_VIEW_TOPIC = "ProfileView";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(PROJECT_PROFILE_VIEW_TOPIC, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
