package faang.school.projectservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class RedisEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public void publish(T event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
