package faang.school.projectservice.publisher;

import faang.school.projectservice.model.event.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher implements MessagePublisher<ProjectViewEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic projectViewTopic;

    @Override
    public void publish(ProjectViewEvent event) {
        redisTemplate.convertAndSend(projectViewTopic.getTopic(), event);
    }
}