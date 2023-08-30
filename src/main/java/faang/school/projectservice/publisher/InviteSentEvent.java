package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.redis.InviteSentEventDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class InviteSentEvent extends AbstractEvent<InviteSentEventDto> {

    private final StageInvitationMapper mapper;
    private final ChannelTopic topicInvitation;

    public InviteSentEvent(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, StageInvitationMapper mapper, ChannelTopic topicInvitation) {
        super(redisTemplate, objectMapper);
        this.mapper = mapper;
        this.topicInvitation = topicInvitation;
    }

    public void publish(StageInvitation stageInvitation) {
        InviteSentEventDto event = mapper.toEventDto(stageInvitation);
        publishInTopic(topicInvitation, event);
    }
}
