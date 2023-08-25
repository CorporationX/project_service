package faang.school.projectservice.service.stageInvitation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stageInvitation.StageInvitationEvent;
import faang.school.projectservice.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StageInvitationEventPublisher implements MessagePublisher<StageInvitationEvent> {
    private final RedisTemplate<String, Object> template;
    private final ObjectMapper mapper;
    @Value("${spring.data.redis.channels.invitation_channel.name}")
    private final String topic;
    @Override
    @Async
    public void publish(StageInvitationEvent event) {
        String json = eventToJson(event);
        template.convertAndSend(topic, json);
    }

    private String eventToJson(StageInvitationEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
