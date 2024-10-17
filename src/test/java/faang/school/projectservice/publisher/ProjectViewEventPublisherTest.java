package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectViewEventPublisherTest {

    private static final String TOPIC_NAME = "TEST";

    @InjectMocks
    private ProjectViewEventPublisher projectViewEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic projectViewEventTopic;

    @Test
    @DisplayName("Should send message to redis")
    void whenGetMessageThenSendMessageToRedis() {
        ProjectViewEvent projectViewEvent = ProjectViewEvent.builder().build();
        when(projectViewEventTopic.getTopic()).thenReturn(TOPIC_NAME);

        projectViewEventPublisher.publish(projectViewEvent);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertEquals(TOPIC_NAME, topicCaptor.getValue());
        assertSame(projectViewEvent, messageCaptor.getValue());
    }
}