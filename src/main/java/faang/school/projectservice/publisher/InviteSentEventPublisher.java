package faang.school.projectservice.publisher;

import faang.school.projectservice.event.InviteSentEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InviteSentEventPublisher extends RedisEventPublisher<InviteSentEvent>{

    public InviteSentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    @Qualifier("invitationChannels") List<ChannelTopic> channelTopics) {
        super(redisTemplate, channelTopics);
    }
}
