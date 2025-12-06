package faang.school.projectservice.integration.jira.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.integration.jira.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Сервис для отправки WebSocket сообщений клиентам
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JiraWebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    
    // ==========================================
    // Task Events
    // ==========================================
    
    /**
     * Отправить уведомление о создании задачи
     */
    public void sendTaskCreated(Long taskId, String jiraIssueKey, Object taskData) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.TASK_CREATED)
            .event("task.created")
            .data(taskData)
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/tasks", message);
        log.debug("WebSocket: Task created notification sent: taskId={}, jiraKey={}", taskId, jiraIssueKey);
    }
    
    /**
     * Отправить уведомление об обновлении задачи
     */
    public void sendTaskUpdated(Long taskId, String jiraIssueKey, Object taskData) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.TASK_UPDATED)
            .event("task.updated")
            .data(taskData)
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/tasks", message);
        log.debug("WebSocket: Task updated notification sent: taskId={}, jiraKey={}", taskId, jiraIssueKey);
    }
    
    /**
     * Отправить уведомление об удалении задачи
     */
    public void sendTaskDeleted(Long taskId, String jiraIssueKey) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.TASK_DELETED)
            .event("task.deleted")
            .data(new TaskDeletedData(taskId, jiraIssueKey))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/tasks", message);
        log.debug("WebSocket: Task deleted notification sent: taskId={}, jiraKey={}", taskId, jiraIssueKey);
    }
    
    /**
     * Отправить уведомление об изменении статуса задачи
     */
    public void sendTaskStatusChanged(Long taskId, String jiraIssueKey, String oldStatus, String newStatus) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.TASK_STATUS_CHANGED)
            .event("task.status.changed")
            .data(new TaskStatusChangedData(taskId, jiraIssueKey, oldStatus, newStatus))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/status", message);
        log.debug("WebSocket: Task status changed notification sent: taskId={}, {} -> {}", 
            taskId, oldStatus, newStatus);
    }
    
    // ==========================================
    // Sync Events
    // ==========================================
    
    /**
     * Отправить уведомление о начале синхронизации проекта
     */
    public void sendSyncStarted(Long projectId) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.SYNC_STARTED)
            .event("sync.started")
            .data(new SyncData(projectId, null, null, null, null))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/sync", message);
        log.debug("WebSocket: Sync started notification sent: projectId={}", projectId);
    }
    
    /**
     * Отправить уведомление о завершении синхронизации проекта
     */
    public void sendSyncCompleted(Long projectId, int totalTasks, int createdTasks, int updatedTasks, int failedTasks) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.SYNC_COMPLETED)
            .event("sync.completed")
            .data(new SyncData(projectId, totalTasks, createdTasks, updatedTasks, failedTasks))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/sync", message);
        log.debug("WebSocket: Sync completed notification sent: projectId={}, total={}", projectId, totalTasks);
    }
    
    /**
     * Отправить уведомление об ошибке синхронизации
     */
    public void sendSyncFailed(Long projectId, String error) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.SYNC_FAILED)
            .event("sync.failed")
            .data(new SyncErrorData(projectId, error))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/sync", message);
        log.debug("WebSocket: Sync failed notification sent: projectId={}, error={}", projectId, error);
    }
    
    /**
     * Отправить уведомление о синхронизации проекта (общее)
     */
    public void sendProjectSynced(Long projectId, int tasksCount) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.PROJECT_SYNCED)
            .event("project.synced")
            .data(new ProjectSyncedData(projectId, tasksCount))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/sync", message);
        log.debug("WebSocket: Project synced notification sent: projectId={}, tasksCount={}", projectId, tasksCount);
    }
    
    // ==========================================
    // Error Events
    // ==========================================
    
    /**
     * Отправить уведомление об ошибке
     */
    public void sendError(String error, String details) {
        WebSocketMessage message = WebSocketMessage.builder()
            .type(WebSocketMessage.MessageType.ERROR)
            .event("error")
            .data(new ErrorData(error, details))
            .timestamp(Instant.now())
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendToTopic("/topic/jira/errors", message);
        log.debug("WebSocket: Error notification sent: error={}", error);
    }
    
    // ==========================================
    // Private Helpers
    // ==========================================
    
    private void sendToTopic(String topic, WebSocketMessage message) {
        try {
            messagingTemplate.convertAndSend(topic, message);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message to topic: {}", topic, e);
        }
    }
    
    /**
     * Отправить сообщение конкретному пользователю
     */
    public void sendToUser(String username, String destination, WebSocketMessage message) {
        try {
            messagingTemplate.convertAndSendToUser(username, destination, message);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message to user: {}", username, e);
        }
    }
    
    // ==========================================
    // Inner Classes for Message Data
    // ==========================================
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TaskDeletedData {
        private Long taskId;
        private String jiraIssueKey;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TaskStatusChangedData {
        private Long taskId;
        private String jiraIssueKey;
        private String oldStatus;
        private String newStatus;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class SyncData {
        private Long projectId;
        private Integer totalTasks;
        private Integer createdTasks;
        private Integer updatedTasks;
        private Integer failedTasks;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class SyncErrorData {
        private Long projectId;
        private String error;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ProjectSyncedData {
        private Long projectId;
        private Integer tasksCount;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ErrorData {
        private String error;
        private String details;
    }
}

