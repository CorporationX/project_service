package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.publisher.achievement.ConglomerateAchievementDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ConglomerateAchievementPublisher implements MessagePublisher<ConglomerateAchievementDto>{
    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic teamChannel;

    @Override
    public void publish(ConglomerateAchievementDto message) {
        redisTemplate.convertAndSend(teamChannel.getTopic(), message);
        log.info("Message published: {}", message);
    }
}
