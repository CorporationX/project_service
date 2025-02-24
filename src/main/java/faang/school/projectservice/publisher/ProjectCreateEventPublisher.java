package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.event.ProjectEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectCreateEventPublisher extends RedisEventPublisher<ProjectEvent> {

    public ProjectCreateEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                       ChannelTopic topic) {
        super(redisTemplate, topic);
    }

    @Override
    public void publish(ProjectEvent event) {
        super.publish(event);
    }
}
