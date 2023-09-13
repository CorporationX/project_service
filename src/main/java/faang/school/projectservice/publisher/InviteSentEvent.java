package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.redis.InviteSentEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class InviteSentEvent extends AbstractPublisher<InviteSentEventDto> {
    public InviteSentEvent(RedisTemplate<String, Object> redisTemplate,
                           ObjectMapper objectMapper,
                           ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
