package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewMessagePublisher extends MessagePublisher<ProjectViewEventDto> {

    private final ChannelTopic projectViewTopic;

    public ProjectViewMessagePublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic projectViewTopic) {
        super(redisTemplate, objectMapper);
        this.projectViewTopic = projectViewTopic;
    }

    @Override
    protected ChannelTopic getEventTopic() {
        return projectViewTopic;
    }
}
