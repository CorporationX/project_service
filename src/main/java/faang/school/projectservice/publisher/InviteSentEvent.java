package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.redis.InviteSentEventDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InviteSentEvent {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StageInvitationMapper mapper;
    private final ObjectMapper objectMapper;
    private final ChannelTopic topicInvitation;

    public void publish(StageInvitation stageInvitation) {
        InviteSentEventDto event = mapper.toEventDto(stageInvitation);
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new faang.school.projectservice.exception.JsonProcessingException(e.getMessage());
        }
        redisTemplate.convertAndSend(topicInvitation.getTopic(), json);
    }
}
