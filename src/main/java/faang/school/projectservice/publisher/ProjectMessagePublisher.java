package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectChannelEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectMessagePublisher extends MessagePublisher<ProjectChannelEventDto> {
    private final ChannelTopic projectChannelTopic;

    public ProjectMessagePublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic projectChannelTopic) {
        super(redisTemplate, objectMapper);
        this.projectChannelTopic = projectChannelTopic;
    }

    @Override
    protected ChannelTopic getEventTopic() {
        return projectChannelTopic;
    }
}