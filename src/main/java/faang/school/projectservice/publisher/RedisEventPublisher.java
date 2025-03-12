package faang.school.projectservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public abstract class RedisEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final List<ChannelTopic> channelTopics;

    public void publish(T event) {
        for (ChannelTopic topic : channelTopics) {
            redisTemplate.convertAndSend(topic.getTopic(), event);
        }
    }
}
