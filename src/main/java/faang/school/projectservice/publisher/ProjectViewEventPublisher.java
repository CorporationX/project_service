package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.event.ProjectViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher implements MessagePublisher<ProjectViewEvent> {

    private final RedisTemplate<String, ProjectViewEvent> template;
    private final ChannelTopic projectViewEventTopic;

    @Autowired
    public ProjectViewEventPublisher(RedisTemplate<String, ProjectViewEvent> template,
                                     @Qualifier("projectViewChannel") ChannelTopic projectViewEventTopic) {
        this.template = template;
        this.projectViewEventTopic = projectViewEventTopic;
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 5000))
    public void publish(ProjectViewEvent event) {
        template.convertAndSend(projectViewEventTopic.getTopic(), event);
    }
}
