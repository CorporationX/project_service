package faang.school.projectservice.publisher.projectview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.ProjectViewEvent;
import faang.school.projectservice.publisher.AbstractEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEvent> {
    public ProjectViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ChannelTopic projectViewTopic,
                                     ObjectMapper objectMapper) {
        super(redisTemplate, projectViewTopic, objectMapper);
    }
}

//projectViewEventDto.setUserId(userId);
//        projectViewEventDto.setProjectId(projectId);
//        projectViewEventDto.setTimestamp(timestamp);
//        log.info("message create");
//        try {
//String jsonMessage = objectMapper.writeValueAsString(projectViewEventDto);
//            projectViewEventPublisher.publish(jsonMessage);
//            log.info("message publish");
//        } catch (
//JsonProcessingException e) {
//        log.error("Error processing JSON message for publishing: {}", e.getMessage());
//        throw new RuntimeException("Error converting projectViewEventDto to JSON", e);
//        }