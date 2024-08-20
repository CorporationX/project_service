package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewMessagePublisher extends ViewMessage<ProjectViewEvent> {

    public ProjectViewMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic projectTopic, ObjectMapper objectMapper) {
        super(redisTemplate, projectTopic, objectMapper);
    }
}
