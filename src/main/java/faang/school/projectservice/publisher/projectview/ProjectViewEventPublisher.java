package faang.school.projectservice.publisher.projectview;

import faang.school.projectservice.dto.projectview.ProjectViewEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String topicName = "project_view_events"; // имя Redis топика

    public ProjectViewEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishProjectViewEvent(ProjectViewEvent event) {
        redisTemplate.convertAndSend(topicName, event);
    }
}