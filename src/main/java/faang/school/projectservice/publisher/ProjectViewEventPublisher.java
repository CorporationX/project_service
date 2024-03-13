package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEvent> {
    @Value("${spring.data.redis.channel.project_view.project_view_channel}")
    String projectViewChannelName;

    public ProjectViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    public void publish(ProjectViewEvent event) {
        log.info("Event was published {}", event);
        convertAndSend(event, projectViewChannelName);
    }
}