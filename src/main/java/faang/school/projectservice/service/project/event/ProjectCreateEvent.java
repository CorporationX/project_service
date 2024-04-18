package faang.school.projectservice.service.project.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectCreateEventDto;
import faang.school.projectservice.pablisher.ProjectEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectCreateEvent {
    private final ProjectEventPublisher projectEventPublisher;
    private final ProjectCreateEventDto projectCreateEventDto;
    private final ObjectMapper objectMapper;

    public void publishProjectCreateEvent(Long userId, Long projectId) {
        projectCreateEventDto.setUserId(userId);
        projectCreateEventDto.setProjectId(projectId);
        log.info("message create");
        try {
            String jsonMessage = objectMapper.writeValueAsString(projectCreateEventDto);
            projectEventPublisher.publish(jsonMessage);
            log.info("message publish");
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON message for publishing: {}", e.getMessage());
            throw new RuntimeException("Error converting projectViewEventDto to JSON", e);
        }
    }
}
