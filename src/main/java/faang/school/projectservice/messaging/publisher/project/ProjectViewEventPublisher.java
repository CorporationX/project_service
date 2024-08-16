package faang.school.projectservice.messaging.publisher.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.event.project.ProjectViewEvent;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.messaging.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher implements EventPublisher<ProjectViewEvent> {

    private final UserContext userContext;
    private final ObjectMapper objectMapper;
    private final ChannelTopic projectViewTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(ProjectViewEvent event) {
        String message;
        try {
            message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(projectViewTopic.getTopic(), message);
            log.info("Published an event: {}", event);
        } catch (IOException e) {
            log.error(String.format(ExceptionMessages.FAILED_EVENT, event.getEventId()), e);
        }
    }

    public void toEventAndPublish(long projectId) {
        publish(ProjectViewEvent.builder()
                .projectId(projectId)
                .viewerId(userContext.getUserId())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
