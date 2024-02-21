package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEvent> {
    private final ChannelTopic profileViewTopic;

    public void publish(ProjectViewEvent projectViewEvent) {
        log.info("Event was published {}", projectViewEvent);
        convertAndSend(projectViewEvent, profileViewTopic.getTopic());
    }

    public void publish(long id, Long ownerId) {
        publish(ProjectViewEvent.builder()
                .projectId(id)
                .ownerId(ownerId)
                .receivedAt(LocalDateTime.now())
                .build());
    }
}