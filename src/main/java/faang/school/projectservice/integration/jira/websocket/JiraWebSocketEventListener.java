package faang.school.projectservice.integration.jira.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.integration.jira.event.JiraProjectSyncedEvent;
import faang.school.projectservice.integration.jira.event.TaskCreatedEvent;
import faang.school.projectservice.integration.jira.event.TaskDeletedEvent;
import faang.school.projectservice.integration.jira.event.TaskStatusChangedEvent;
import faang.school.projectservice.integration.jira.event.TaskUpdatedEvent;
import faang.school.projectservice.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event Listener для отправки событий через WebSocket
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JiraWebSocketEventListener {
    
    private final JiraWebSocketService webSocketService;
    private final ObjectMapper objectMapper;
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        log.debug("WebSocket: Processing TaskCreatedEvent: taskId={}", event.getTask().getId());
        
        try {
            TaskData taskData = toTaskData(event.getTask(), event.getJiraIssueKey());
            webSocketService.sendTaskCreated(
                event.getTask().getId(),
                event.getJiraIssueKey(),
                taskData
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for TaskCreatedEvent", e);
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onTaskUpdated(TaskUpdatedEvent event) {
        log.debug("WebSocket: Processing TaskUpdatedEvent: taskId={}", event.getTask().getId());
        
        try {
            TaskData taskData = toTaskData(event.getTask(), event.getJiraIssueKey());
            webSocketService.sendTaskUpdated(
                event.getTask().getId(),
                event.getJiraIssueKey(),
                taskData
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for TaskUpdatedEvent", e);
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onTaskDeleted(TaskDeletedEvent event) {
        log.debug("WebSocket: Processing TaskDeletedEvent: taskId={}", event.getTaskId());
        
        try {
            webSocketService.sendTaskDeleted(
                event.getTaskId(),
                event.getJiraIssueKey()
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for TaskDeletedEvent", e);
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onTaskStatusChanged(TaskStatusChangedEvent event) {
        log.debug("WebSocket: Processing TaskStatusChangedEvent: taskId={}, {} -> {}", 
            event.getTask().getId(), event.getOldStatus(), event.getNewStatus());
        
        try {
            String jiraIssueKey = event.getTask().getJiraIssueKey();
            webSocketService.sendTaskStatusChanged(
                event.getTask().getId(),
                jiraIssueKey != null ? jiraIssueKey : "",
                event.getOldStatus() != null ? event.getOldStatus().name() : "UNKNOWN",
                event.getNewStatus() != null ? event.getNewStatus().name() : "UNKNOWN"
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for TaskStatusChangedEvent", e);
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onProjectSynced(JiraProjectSyncedEvent event) {
        log.debug("WebSocket: Processing JiraProjectSyncedEvent: projectId={}, tasksCount={}", 
            event.getProjectId(), event.getTasksCount());
        
        try {
            webSocketService.sendProjectSynced(
                event.getProjectId(),
                event.getTasksCount()
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for JiraProjectSyncedEvent", e);
        }
    }
    
    // ==========================================
    // Private Helpers
    // ==========================================
    
    private TaskData toTaskData(Task task, String jiraIssueKey) {
        return TaskData.builder()
            .id(task.getId())
            .name(task.getName())
            .description(task.getDescription())
            .status(task.getStatus() != null ? task.getStatus().name() : null)
            .jiraIssueKey(jiraIssueKey != null ? jiraIssueKey : task.getJiraIssueKey())
            .projectId(task.getProject() != null ? task.getProject().getId() : null)
            .performerUserId(task.getPerformerUserId())
            .reporterUserId(task.getReporterUserId())
            .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TaskData {
        private Long id;
        private String name;
        private String description;
        private String status;
        private String jiraIssueKey;
        private Long projectId;
        private Long performerUserId;
        private Long reporterUserId;
    }
}

