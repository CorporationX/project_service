package faang.school.projectservice.service.project.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.event.ProjectViewEvent;
import faang.school.projectservice.publisher.projectview.ProjectViewEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ProjectViewEventTest {

    @Mock
    private ProjectViewEventPublisher mockPublisher;

    @Spy
    private ObjectMapper mockMapper;

    @Mock
    private ProjectViewEvent projectViewEventDto;

    @InjectMocks
    private ProjectViewEvent projectViewEvent;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Test
    public void testPublishProjectViewEvent_Success() throws JsonProcessingException {
        Long userId = 1L;
        Long projectId = 2L;
        LocalDateTime timestamp = LocalDateTime.now();
        String expectedJson = "{'userId': 1, 'projectId': 2, 'timestamp': '2024-04-17T10:42:13.456'}";
        mockMapper.registerModule(new JavaTimeModule());
        Mockito.when(mockMapper.writeValueAsString(any(ProjectViewEvent.class))).thenReturn(expectedJson);

        projectViewEvent.publishProjectViewEvent(userId, projectId, timestamp);

        Mockito.verify(mockMapper).writeValueAsString(any(ProjectViewEvent.class));
        Mockito.verify(mockPublisher).publish(messageCaptor.capture());

        String capturedMessage = messageCaptor.getValue();
        assertEquals(expectedJson, capturedMessage);
    }
}
