package faang.school.projectservice.pablisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectCreateEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProjectEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;


    public void publish(ProjectCreateEventDto event) {
        try {
            log.info("message create");
            redisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(event));
            log.info("message publish");
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON message for publishing: {}", e.getMessage());
            throw new RuntimeException("Error converting projectViewEventDto to JSON", e);
        }
        log.info("send message to topic: {}", channelTopic.getTopic());
    }
}
