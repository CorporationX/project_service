package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.event.InviteSentEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class InviteSentEventPublisher extends AbstractEventPublisher<InviteSentEvent> {
    private final ChannelTopic invitationTopic;

    public InviteSentEventPublisher(ObjectMapper objectMapper,
                                    StringRedisTemplate redisTemplate,
                                    ChannelTopic invitationTopic) {
        super(objectMapper, redisTemplate);
        this.invitationTopic = invitationTopic;
    }

    @Override
    protected String getTopic() {
        return invitationTopic.getTopic();
    }
}
