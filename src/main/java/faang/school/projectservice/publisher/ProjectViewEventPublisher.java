package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.ProjectViewEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEventDto> {
    private final ChannelTopic projectViewTopic;

    @Autowired
    public ProjectViewEventPublisher(RedisTemplate<String, Object> template,
                                     ObjectMapper objectMapper,
                                     @Qualifier("projectViewChannel") ChannelTopic projectViewTopic) {
        super(template, objectMapper);
        this.projectViewTopic = projectViewTopic;
    }

    public void sendEvent(ProjectViewEventDto projectViewEventDto) {
        publish(projectViewTopic, projectViewEventDto);
    }
}