package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewProfileEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ProjectProfileViewPublisherTest {
    private final static String PROJECT_PROFILE_VIEW_TOPIC = "ProfileView";
    @Mock
    private RedisTemplate<String, Object> redisTemplateMock;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ProjectProfileViewPublisher projectProfileViewPublisher;

    @Test
    @DisplayName("Test publish event")
    void testPublish() throws JsonProcessingException {

        Long projectId = 222L;
        Long userId = 1L;
        String json = "{}";
        ProjectViewProfileEvent event = new ProjectViewProfileEvent(
                projectId, userId, LocalDateTime.now());

        Mockito.when(objectMapper.writeValueAsString(event)).thenReturn(json);
        projectProfileViewPublisher.publish(event);
        Mockito.verify(redisTemplateMock, Mockito.times(1))
                .convertAndSend(PROJECT_PROFILE_VIEW_TOPIC, json);
    }
}