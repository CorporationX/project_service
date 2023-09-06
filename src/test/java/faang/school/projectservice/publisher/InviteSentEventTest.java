package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.redis.InviteSentEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
class InviteSentEventTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic topic;

    private InviteSentEvent inviteSentEvent;

    @BeforeEach
    void setUp() {
        inviteSentEvent = new InviteSentEvent(redisTemplate, objectMapper, topic);
    }

    @Test
    void publish() throws JsonProcessingException {
        InviteSentEventDto eventDto = InviteSentEventDto.builder()
                .authorId(1L)
                .invitedId(2L)
                .projectId(1L)
                .build();

        Mockito.when(objectMapper.writeValueAsString(eventDto)).thenReturn("json");

        inviteSentEvent.publish(eventDto);

        Mockito.verify(redisTemplate).convertAndSend(topic.getTopic(), "json");
    }
}