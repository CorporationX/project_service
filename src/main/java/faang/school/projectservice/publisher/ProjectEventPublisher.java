package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.event.ProjectEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectEventPublisher extends AbstractEventPublisher<ProjectEvent>{

    private final ChannelTopic projectTopic;

    public ProjectEventPublisher(ObjectMapper objectMapper, StringRedisTemplate redisTemplate, @Qualifier("projectTopic") ChannelTopic projectTopic) {
        super(objectMapper, redisTemplate);
        this.projectTopic = projectTopic;
    }

    @Override
    protected String getTopic() {
        return projectTopic.getTopic();
    }
}
