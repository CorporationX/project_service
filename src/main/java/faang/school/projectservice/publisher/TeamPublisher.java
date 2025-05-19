package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import faang.school.projectservice.dto.team.TeamEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamPublisher implements MessagePublisher<TeamEvent> {

    private static final Logger log = LoggerFactory.getLogger(TeamPublisher.class);
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.team-channel.name}")
    private String teamChannel;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(TeamEvent event){
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(teamChannel, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
