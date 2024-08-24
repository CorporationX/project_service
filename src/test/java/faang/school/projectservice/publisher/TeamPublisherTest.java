package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.dto.TeamEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TeamPublisherTest {
    private static final String CHANNEL_NAME = "team_channel";
    private static final String JSON_STRING = "{\"userId\":1,\"projectId\":1,\"teamId\":1}";
    private static final long RANDOM_ID = 1L;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topicForTeamEvent;

    @InjectMocks
    private TeamPublisher teamPublisher;

    @BeforeEach
    void setUp() {
        Mockito.when(topicForTeamEvent.getTopic()).thenReturn(CHANNEL_NAME);
    }

    @Test
    void testValidPublish() throws JsonProcessingException {
        //Arrange
        TeamEvent teamEvent = new TeamEvent(RANDOM_ID, RANDOM_ID, RANDOM_ID);
        //Act
        teamPublisher.publish(teamEvent);
        //Assert
        Mockito.verify(redisTemplate, times(1)).convertAndSend(CHANNEL_NAME, JSON_STRING);
    }
}