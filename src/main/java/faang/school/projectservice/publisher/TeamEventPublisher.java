package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.team.TeamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamEventPublisher implements EventPublisher<TeamEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.team-channel}")
    private String teamEventsTopic;

    @Override
    public void publish(TeamEvent teamEvent) {
        redisTemplate.convertAndSend(teamEventsTopic, teamEvent);
        log.debug("Team event has been sent. Topic: {}, event: {}", teamEventsTopic, teamEvent);
    }
}
