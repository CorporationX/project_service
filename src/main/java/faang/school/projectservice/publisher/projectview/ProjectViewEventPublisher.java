package faang.school.projectservice.publisher.projectview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.AbstractEventPublisher;
import faang.school.projectservice.service.project.event.ProjectViewEvent;
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
