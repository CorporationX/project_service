package faang.school.projectservice.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.Publisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectViewEvent {
    Publisher publisher;
    public void publishProjectViewEvent(Long userId, Long viewedProfileId, LocalDateTime timestamp) {
        String jsonMessage = createJsonMessage(userId, viewedProfileId, timestamp);

        publisher.publish(jsonMessage);
    }

    private String createJsonMessage(Long userId, Long viewedProfileId, LocalDateTime timestamp) {
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("userId", userId);
        jsonData.put("viewedProfileId", viewedProfileId);
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
