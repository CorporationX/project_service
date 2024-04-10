package faang.school.projectservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class Publisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @SneakyThrows
    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend("view-project", message);
        log.info("send message to topic");
    }
}
