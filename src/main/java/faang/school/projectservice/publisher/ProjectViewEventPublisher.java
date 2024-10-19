package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.event.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher implements MessagePublisher<ProjectViewEvent> {

    private final RedisTemplate<String, ProjectViewEvent> template;
    private final ChannelTopic projectViewEventTopic;

    @Override
    public void publish(ProjectViewEvent event) {
        template.convertAndSend(projectViewEventTopic.getTopic(), event);
    }
}
