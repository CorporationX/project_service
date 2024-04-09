package faang.school.projectservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
@Slf4j
@RequiredArgsConstructor
@Component
public class Publisher implements MessagePublisher{
    private RedisTemplate<String,Object> redisTemplate;
    private ChannelTopic topic;
    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(),message);
        log.info("send message to topic");
    }
}
