package faang.school.projectservice.publisher;

import faang.school.projectservice.event.ProjectEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectCreateEventPublisher extends RedisEventPublisher<ProjectEvent> {

    public ProjectCreateEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                       @Qualifier("projectChannels") List<ChannelTopic> channelTopics) {
        super(redisTemplate, channelTopics);
    }
}
