package faang.school.projectservice.service.project.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectViewEvent {
    private final ProjectViewEventPublisher projectViewEventPublisher;
    public void publishProjectViewEvent(Long userId, Long projectId, LocalDateTime timestamp) {
        String jsonMessage = createJsonMessage(userId, projectId, timestamp);
        log.info("message create");
        projectViewEventPublisher.publish(jsonMessage);
        log.info("message publish");
    }

    private String createJsonMessage(Long userId, Long projectId, LocalDateTime timestamp) {
        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("userId", userId.toString());
        jsonData.put("projectId", projectId.toString());
        jsonData.put("timestamp", timestamp.toString());

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Error creating JSON message");
            throw new RuntimeException("Error creating JSON message", e);
        }
    }
}
