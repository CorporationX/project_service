package faang.school.projectservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectEventPublisher extends MessagePublisher<ProjectEvent> {
    public ProjectEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ChannelTopic projectTopic,
                                 ObjectMapper objectMapper) {
        super(redisTemplate, projectTopic, objectMapper);
    }
}
