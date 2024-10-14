package faang.school.projectservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListener {
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleMessage(String jsonEvent) {
        ProjectEvent event = readEvent(jsonEvent);
        log.info("Received message from channel: {}", jsonEvent);
    }

    private ProjectEvent readEvent(String jsonEvent) {
        try {
            log.info("reading message {}", jsonEvent);
            return objectMapper.readValue(jsonEvent, ProjectEvent.class);
        } catch (JsonProcessingException exception) {
            log.error("message was not downloaded {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
