package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.event.TeamEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class TeamMessagePublisher extends GenericMessagePublisher<TeamEvent> {
    private final ChannelTopic teamTopic;

    public TeamMessagePublisher(RedisTemplate<String, Object> redisTemplate,
                                ObjectMapper objectMapper,
                                ChannelTopic teamTopic) {
        super(redisTemplate, objectMapper);
        this.teamTopic = teamTopic;
    }

    @Override
    protected ChannelTopic getTopic() {
        return teamTopic;
    }
}
