package faang.school.projectservice.integration.jira.websocket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO для WebSocket сообщений
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage {
    
    private MessageType type;
    private String event;
    private Object data;
    private Instant timestamp;
    private String correlationId;
    
    public enum MessageType {
        TASK_CREATED,
        TASK_UPDATED,
        TASK_DELETED,
        TASK_STATUS_CHANGED,
        PROJECT_SYNCED,
        SYNC_STARTED,
        SYNC_COMPLETED,
        SYNC_FAILED,
        ERROR,
        INFO
    }
}

