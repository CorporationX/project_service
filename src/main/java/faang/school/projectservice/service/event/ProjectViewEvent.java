package faang.school.projectservice.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.Publisher;
import jakarta.servlet.http.HttpServletRequest;
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
    private final Publisher publisher;
    public void publishProjectViewEvent(String userId, Long projectId, LocalDateTime timestamp) {
        String jsonMessage = createJsonMessage(userId, projectId, timestamp);
        log.info("message create");
        publisher.publish(jsonMessage);
        log.info("message publish");
    }

    private String createJsonMessage(String userId, Long projectId, LocalDateTime timestamp) {
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("userId", userId);
        jsonData.put("projectId", projectId);
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
