package faang.school.projectservice.publisher;

import faang.school.projectservice.event.TaskCompletedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskEventPublisher extends RedisEventPublisher<TaskCompletedEvent> {

    public TaskEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              @Qualifier("taskChannels") List<ChannelTopic> channelTopics) {
        super(redisTemplate, channelTopics);
    }
}
