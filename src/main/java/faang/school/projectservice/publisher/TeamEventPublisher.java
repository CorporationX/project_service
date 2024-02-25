package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.client.event.TeamEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.team_channel.name}")
    private String teamTopic;

    public void publish(TeamEventDto teamEventDto) {
        try {
            String json = objectMapper.writeValueAsString(teamEventDto);
            redisTemplate.convertAndSend(teamTopic, json);
            log.info("Отправлено событие создания команды с ID: {}, организатор команды с ID: {}, проект с ID: {}",
                    teamEventDto.getTeamId(),
                    teamEventDto.getUserId(),
                    teamEventDto.getProjectId()
            );
        } catch (JsonProcessingException e) {
            log.error("Ошибка в обработке ", e);
            throw new RuntimeException(e);
        }
    }
}