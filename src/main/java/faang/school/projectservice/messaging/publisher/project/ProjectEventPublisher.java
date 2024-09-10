package faang.school.projectservice.messaging.publisher.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.event.project.ProjectEvent;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.messaging.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectEventPublisher implements EventPublisher<ProjectEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.project_topic}")
    private String projectTopic;

    @Override
    public void publish(ProjectEvent event) {
        String message;
        try {
            message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(projectTopic, message);
            log.info("Published an event: {}", event);
        } catch (JsonProcessingException e) {
            log.error(String.format(ExceptionMessages.FAILED_EVENT, event.getEventId()), e);
        }
    }
}
