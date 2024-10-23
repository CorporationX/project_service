package faang.school.projectservice.publisher;

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
    private final RedisTemplate<String, Object> redisTemplateConfig;
    private final RedisProperties redisProperties;

    public void publish(ProjectEvent event) {
        String channelName = redisProperties.getChannels().get("project-event-channel");
        redisTemplateConfig.convertAndSend(channelName, event);
        log.info("Published event {}", event);
    }
}
