package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class ProjectViewEventPublisher implements EventPublisher<ProjectViewEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(ProjectViewEvent projectViewEvent) {
        redisTemplate.convertAndSend(topic.getTopic(), projectViewEvent);
    }
}