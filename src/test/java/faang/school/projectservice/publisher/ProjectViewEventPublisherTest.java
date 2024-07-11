package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.ProjectViewEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectViewEventPublisherTest {
    @InjectMocks
    private ProjectViewEventPublisher projectViewEventPublisher;

    @Mock
    private RedisTemplate<String, Object> template;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic channelTopic;

    @Test
    public void testSendEvent() throws JsonProcessingException {
        ProjectViewEventDto projectViewEventDto = ProjectViewEventDto.builder().projectId(1L).build();
        String json = objectMapper.writeValueAsString(projectViewEventDto);
        projectViewEventPublisher.sendEvent(projectViewEventDto);

        verify(template, times(1)).convertAndSend(channelTopic.getTopic(), json);
    }
}