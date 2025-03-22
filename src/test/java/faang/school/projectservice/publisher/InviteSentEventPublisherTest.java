package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.event.InviteSentEvent;
import faang.school.projectservice.exception.PublishEventException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteSentEventPublisherTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ChannelTopic invitationTopic;

    @InjectMocks
    private InviteSentEventPublisher eventPublisher;

    @Test
    void publish_ShouldSendEventToRedis() throws JsonProcessingException {
        InviteSentEvent event = new InviteSentEvent(1L, 2L, 3L);
        String json = "{\"userId\":1,\"authorId\":2,\"projectId\":3}";
        String topic = "invitation-topic";

        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        when(invitationTopic.getTopic()).thenReturn(topic);

        eventPublisher.publish(event);

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate).convertAndSend(topic, json);
    }

    @Test
    void publish_ShouldThrowPublishEventExceptionOnSerializationError() throws JsonProcessingException {
        InviteSentEvent event = new InviteSentEvent(1L, 2L, 3L);
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("Serialization error") {
                });

        PublishEventException exception = assertThrows(PublishEventException.class,
                () -> eventPublisher.publish(event));
        assertEquals("Ошибка при публикации ивента", exception.getMessage());
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}