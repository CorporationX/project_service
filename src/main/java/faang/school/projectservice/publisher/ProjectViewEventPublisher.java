package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("spring.data.redis.topics.project_view_topic")
    private String projectViewTopic;

    public void publish(ProjectViewEvent projectViewEvent) {
        redisTemplate.convertAndSend(projectViewTopic, projectViewEvent);
    }
}
