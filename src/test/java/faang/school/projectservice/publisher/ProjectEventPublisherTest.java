package faang.school.projectservice.publisher;

import faang.school.projectservice.config.redis.RedisProperties;
import faang.school.projectservice.dto.ProjectEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ProjectEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private ProjectEventPublisher projectEventPublisher;
    private ProjectEvent projectEvent;

    @BeforeEach
    public void setUp() {
        projectEvent = new ProjectEvent();
        RedisProperties redisProperties = new RedisProperties();
        Map<String, String> channelsMap = new HashMap<>();
        channelsMap.put("project-event-channel", "project-event");
        redisProperties.setChannels(channelsMap);
        projectEventPublisher = new ProjectEventPublisher(redisTemplate, redisProperties);
    }

    @Test
    void publishTest() {
        projectEventPublisher.publish(projectEvent);

        verify(redisTemplate).convertAndSend("project-event", projectEvent);
    }
}
