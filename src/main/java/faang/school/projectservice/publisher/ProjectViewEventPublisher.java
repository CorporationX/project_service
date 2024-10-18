package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher implements MessagePublisher<ProjectViewEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic projectViewEventTopic;

    @Override
    public void publish(ProjectViewEvent message) {
        log.debug("Sending message - {} to topic - {}", message, projectViewEventTopic.getTopic());
        redisTemplate.convertAndSend(projectViewEventTopic.getTopic(), message);
    }
}
