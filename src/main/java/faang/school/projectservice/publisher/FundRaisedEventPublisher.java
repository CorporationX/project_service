package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.model.event.FundRaisedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class FundRaisedEventPublisher extends AbstractEventPublisher<FundRaisedEvent> {
    public FundRaisedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper,
                                    @Qualifier("fundRaisedChannelTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
