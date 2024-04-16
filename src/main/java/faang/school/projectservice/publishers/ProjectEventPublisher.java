package faang.school.projectservice.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectEventPublisher implements MessagePublisher{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic projectTopic;
    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(projectTopic.getTopic(), message);
    }
}
