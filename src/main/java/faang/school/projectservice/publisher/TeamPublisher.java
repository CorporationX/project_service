package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import faang.school.projectservice.dto.team.TeamEvent;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TeamPublisher implements MessagePublisher<TeamEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String teamChannel;
    private final ObjectMapper objectMapper;

    public TeamPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channels.team-channel.name}") String teamChannel, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.teamChannel = teamChannel;
        this.objectMapper = objectMapper;
    }

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
