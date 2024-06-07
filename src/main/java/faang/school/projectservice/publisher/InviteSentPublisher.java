package faang.school.projectservice.publisher;

import faang.school.projectservice.event.InviteSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InviteSentPublisher implements MessagePublisher<InviteSentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic inviteSentTopic;

    @Override
    public void publish(InviteSentEvent event) {
        redisTemplate.convertAndSend(inviteSentTopic.getTopic(), event);
        log.info("Published goal completed event: {}", event);
    }
}
