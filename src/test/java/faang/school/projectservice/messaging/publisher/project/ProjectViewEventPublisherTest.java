package faang.school.projectservice.messaging.publisher.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.event.project.ProjectViewEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectViewEventPublisherTest {

    @Mock
    private UserContext userContext;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic projectViewTopic;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Captor
    private ArgumentCaptor<ProjectViewEvent> eventCaptor;
    @InjectMocks
    private ProjectViewEventPublisher publisher;

    @Test
    void testPublish_Success() throws IOException {
        ProjectViewEvent event = ProjectViewEvent.builder().build();
        String serializedEvent = "serialized event";
        String topic = "project.view";

        when(objectMapper.writeValueAsString(event)).thenReturn(serializedEvent);
        when(projectViewTopic.getTopic()).thenReturn(topic);

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(topic, serializedEvent);
    }

    @Test
    void testPublish_IOExceptionHandled() throws IOException {
        ProjectViewEvent event = ProjectViewEvent.builder().build();

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Test exception") {});

        publisher.publish(event);

        verify(redisTemplate, never()).convertAndSend(any(), any());
    }

    @Test
    void testToEventAndPublish() throws IOException {
        long projectId = 123L;
        long viewerId = 456L;
        String serializedEvent = "serialized event";
        String topic = "project.view";

        when(userContext.getUserId()).thenReturn(viewerId);
        when(objectMapper.writeValueAsString(any(ProjectViewEvent.class))).thenReturn(serializedEvent);
        when(projectViewTopic.getTopic()).thenReturn(topic);

        publisher.toEventAndPublish(projectId);

        verify(redisTemplate).convertAndSend(topic, serializedEvent);
        verify(objectMapper).writeValueAsString(eventCaptor.capture());
        assertEquals(projectId, eventCaptor.getValue().getProjectId());
        assertEquals(viewerId, eventCaptor.getValue().getViewerId());
    }
}