package faang.school.projectservice.publisher;

import faang.school.projectservice.model.event.ManagerAchievementEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerEventPublisher implements MessagePublisher<ManagerAchievementEvent> {

    private final ChannelTopic achievementChannelTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(ManagerAchievementEvent event) {
        redisTemplate.convertAndSend(achievementChannelTopic.getTopic(), event);
    }
}
