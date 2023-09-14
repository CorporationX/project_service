package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.redis.InviteSentEventDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class InviteSentPublisher extends AbstractPublisher<InviteSentEventDto> {

    private final StageInvitationMapper mapper;

    public InviteSentPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                               StageInvitationMapper mapper,
                               @Value("${spring.data.redis.channels.invitation_channel}") String topicChannelName) {
        super(redisTemplate, objectMapper, topicChannelName);
        this.mapper = mapper;
    }

    public void publish(StageInvitation stageInvitation) {
        InviteSentEventDto event = mapper.toEventDto(stageInvitation);
        publishInTopic(event);
    }
}
