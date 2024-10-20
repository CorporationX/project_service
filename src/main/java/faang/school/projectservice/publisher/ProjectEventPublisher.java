package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.ProjectEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service("ProjectEventPublisher")
public class ProjectEventPublisher extends AbstractMessagePublisher<ProjectEvent> {

    public ProjectEventPublisher(@Qualifier("projectEventPublisherRedisTemplate")
                                 RedisTemplate<String, String> redisTemplate,
                                 @Qualifier("projectChannel") ChannelTopic topic,
                                 ObjectMapper objectMapper
    ) {
        super(redisTemplate, topic, objectMapper);
    }

}
