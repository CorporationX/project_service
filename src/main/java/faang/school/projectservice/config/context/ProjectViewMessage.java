package faang.school.projectservice.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.publisher.ViewMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewMessage extends ViewMessage<ProjectViewEvent> {
    public ProjectViewMessage(RedisTemplate<String, Object> redisTemplate, ChannelTopic projectTopic, ObjectMapper objectMapper) {
        super(redisTemplate, projectTopic, objectMapper);
    }
}
