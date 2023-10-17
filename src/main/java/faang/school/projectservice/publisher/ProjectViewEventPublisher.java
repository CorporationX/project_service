package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectViewEventPublisher {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic projectViewTopic;

    public void publish(ProjectViewDto projectViewEvent) {
        try {
            String viewEvent = objectMapper.writeValueAsString(projectViewEvent);
            redisTemplate.convertAndSend(projectViewTopic.getTopic(), viewEvent);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
    }
}
