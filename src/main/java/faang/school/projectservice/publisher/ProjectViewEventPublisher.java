package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectViewEventPublisher implements EventPublisher<String> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profileViewTopic;

    @Override
    public void publish(String str) {
        redisTemplate.convertAndSend(profileViewTopic.getTopic(), str);
    }
}