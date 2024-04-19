package faang.school.projectservice.service.project.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.ProjectViewEventDto;
import faang.school.projectservice.publisher.projectview.ProjectViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectViewEvent {
    private final ProjectViewEventPublisher projectViewEventPublisher;
    private final ProjectViewEventDto projectViewEventDto;
    private final ObjectMapper objectMapper;

    public void publishProjectViewEvent(Long userId, Long projectId, LocalDateTime timestamp) {
        projectViewEventDto.setUserId(userId);
        projectViewEventDto.setProjectId(projectId);
        projectViewEventDto.setTimestamp(timestamp);
        log.info("message create");
        try {
            String jsonMessage = objectMapper.writeValueAsString(projectViewEventDto);
            projectViewEventPublisher.publish(jsonMessage);
            log.info("message publish");
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON message for publishing: {}", e.getMessage());
            throw new RuntimeException("Error converting projectViewEventDto to JSON", e);
        }
    }
}
