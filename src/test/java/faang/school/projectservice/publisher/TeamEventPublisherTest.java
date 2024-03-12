package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.client.event.TeamEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TeamEventPublisherTest {

    @InjectMocks
    private TeamEventPublisher teamEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private String teamTopic;
    private TeamEventDto teamEventDto;
    private String json;

    @BeforeEach
    void setUp() {
        teamEventDto = TeamEventDto.builder()
                .userId(1L)
                .projectId(1L)
                .teamId(1L)
                .build();
        json = "{\"userId\":1,\"teamId\":1,\"projectId\":1}";
    }

    @Test
    void testPublishSendsMessageToRedisSuccessfully() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(teamEventDto)).thenReturn(json);

        teamEventPublisher.publish(teamEventDto);

        verify(redisTemplate, times(1)).convertAndSend(teamTopic, json);
        verify(objectMapper, times(1)).writeValueAsString(teamEventDto);
    }
}