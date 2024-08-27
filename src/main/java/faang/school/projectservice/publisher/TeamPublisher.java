package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.TeamEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamPublisher implements MessagePublisher<TeamEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topicForTeamEvent;

    public void publish(TeamEvent event) throws JsonProcessingException {
        String jsonEvent = new ObjectMapper().writeValueAsString(event);
        redisTemplate.convertAndSend(topicForTeamEvent.getTopic(), jsonEvent);
    }
}