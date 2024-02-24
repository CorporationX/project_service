package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEvent> {
    private final ChannelTopic channelTopic;

    public ProjectViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ObjectMapper objectMapper,
                                     ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper);
        this.channelTopic = channelTopic;
    }

    @Override
    public void publish(ProjectViewEvent event) {
        log.info("Event was published {}", event);
        convertAndSend(event, channelTopic.getTopic());
    }

    public void publish(long id, Long ownerId) {
        this.publish(ProjectViewEvent.builder()
                .projectId(id)
                .ownerId(ownerId)
                .receivedAt(LocalDateTime.now())
                .build());
    }
}