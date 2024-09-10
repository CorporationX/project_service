package faang.school.projectservice.messaging.publisher.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.event.project.ProjectEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectEventPublisherTest {

    @InjectMocks
    private ProjectEventPublisher projectEventPublisher;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testPublish() throws JsonProcessingException {
        var projectEvent = ProjectEvent.builder().build();
        String message = "message";

        when(objectMapper.writeValueAsString(projectEvent)).thenReturn(message);
        when(kafkaTemplate.send(any(), eq(message))).thenReturn(new CompletableFuture<>());

        projectEventPublisher.publish(projectEvent);

        verify(objectMapper).writeValueAsString(projectEvent);
        verify(kafkaTemplate).send(any(), eq(message));
    }
}