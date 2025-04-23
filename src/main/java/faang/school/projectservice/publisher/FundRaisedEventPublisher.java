package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.event.FundRaisedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FundRaisedEventPublisher {

    @Value("${spring.data.redis.channels.fundRaised}")
    private String fundRaisedEventTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publishFundRaisedEvent(FundRaisedEvent event) {
        redisTemplate.convertAndSend(fundRaisedEventTopic, event);
    }
}
