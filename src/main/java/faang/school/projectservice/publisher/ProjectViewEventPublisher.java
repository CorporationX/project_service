package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.event.ProjectViewEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher extends RedisMessagePublisher<ProjectViewEvent> {

    public ProjectViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ChannelTopic projectViewTopic) {
        super(redisTemplate, projectViewTopic);
    }
}
