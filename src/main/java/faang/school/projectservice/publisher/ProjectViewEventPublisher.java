package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEvent> {
    private final ChannelTopic profileViewTopic;

    public void publish(ProjectViewEvent projectViewEvent) {
        convertAndSend(projectViewEvent, profileViewTopic.getTopic());
    }
}