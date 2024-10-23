package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.event.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher implements MessagePublisher<ProjectViewEvent> {

    private final RedisTemplate<String, ProjectViewEvent> redisTemplateConfig;
    private final ChannelTopic topic;

    @Override
    public void publish(ProjectViewEvent message) {
        log.info("Publishing message to topic: {}", topic.getTopic());
        redisTemplateConfig.convertAndSend(topic.getTopic(), message);
        log.info("Message published successfully to topic: {}", topic.getTopic());
    }
}
