package faang.school.projectservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class RedisMessagePublisher<T> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ChannelTopic topic;

    public void publish(T event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
