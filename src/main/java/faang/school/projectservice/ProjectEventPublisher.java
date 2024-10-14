package faang.school.projectservice;

import faang.school.projectservice.config.redis.RedisProperties;
import faang.school.projectservice.dto.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(ProjectEvent event) {
        String channelName = redisProperties.getChannels().get("project-event-channel");
        redisTemplate.convertAndSend(channelName, event);
        log.info("Published event {}", event);
    }
}
