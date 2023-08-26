package faang.school.projectservice.service.stageInvitation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stageInvitation.StageInvitationEvent;
import faang.school.projectservice.service.EventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationEventPublisher extends EventPublisher<StageInvitationEvent> {

    public StageInvitationEventPublisher(RedisTemplate<String, Object> template,
                                         ObjectMapper mapper,
                                         @Value("${spring.data.redis.channels.invitation_channel.name}") String topic) {
        super(template, mapper, topic);
    }
}
