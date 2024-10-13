package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.event.ProjectViewEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectViewEventPublisherTest {

    private final String topicName = "test-topic";
    @Mock
    private RedisTemplate<String, ProjectViewEvent> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private ProjectViewEventPublisher publisher;

    private ProjectViewEvent event;

    @BeforeEach
    public void setUp() {
        event = new ProjectViewEvent(1L, 2L, LocalDateTime.now());
        when(topic.getTopic()).thenReturn(topicName);
    }

    @Test
    public void publish_shouldPublishMessageSuccessfully() {
        publisher.publish(event);

        verify(redisTemplate).convertAndSend(topicName, event);
        verify(topic, atLeast(1)).getTopic();
    }
}
