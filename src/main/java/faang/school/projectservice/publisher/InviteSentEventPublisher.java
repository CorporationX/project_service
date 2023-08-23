package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Data
public class InviteSentEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> invitationTemplate;
    private final ChannelTopic invitationTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String message) {
        invitationTemplate.convertAndSend(invitationTopic.getTopic(), message);
    }

    @Override
    public void publish(Object event) {
        String eventString;
        try {
            eventString = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        invitationTemplate.convertAndSend(invitationTopic.getTopic(), eventString);
    }
}
