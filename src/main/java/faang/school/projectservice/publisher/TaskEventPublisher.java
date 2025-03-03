package faang.school.projectservice.publisher;

import faang.school.projectservice.event.TaskCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(Long userId, Long taskId, Long projectId) {
        TaskCompletedEvent taskCompletedEvent = new TaskCompletedEvent(userId, taskId, projectId);
        redisTemplate.convertAndSend(channelTopic.getTopic(), taskCompletedEvent);
    }
}
