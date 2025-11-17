package faang.school.projectservice.integration.jira.event;

import faang.school.projectservice.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Обработчик событий Jira интеграции
 * Все методы выполняются асинхронно
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JiraEventListener {
    
    // Здесь можно инжектить сервисы для уведомлений, аудита и т.д.
    // private final NotificationService notificationService;
    // private final AuditService auditService;
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        log.info("Processing TaskCreatedEvent: taskId={}, jiraKey={}", 
            event.getTask().getId(), 
            event.getJiraIssueKey()
        );
        
        try {
            // Отправить уведомление assignee
            if (event.getTask().getPerformerUserId() != null) {
                log.debug("Sending notification to assignee: {}", event.getTask().getPerformerUserId());
                // notificationService.notifyTaskAssigned(event.getTask());
            }
            
            // Записать в audit log
            log.debug("Recording task creation in audit log");
            // auditService.logTaskCreation(event.getTask(), event.getJiraIssueKey());
            
            // Обновить статистику проекта
            log.debug("Updating project statistics");
            // statisticsService.incrementTaskCount(event.getTask().getProject().getId());
            
            log.info("TaskCreatedEvent processed successfully");
            
        } catch (Exception e) {
            log.error("Failed to process TaskCreatedEvent", e);
            // Не бросаем exception чтобы не сломать основной flow
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onTaskUpdated(TaskUpdatedEvent event) {
        log.info("Processing TaskUpdatedEvent: taskId={}", event.getTask().getId());
        
        try {
            // Записать изменения в audit log
            log.debug("Recording task update in audit log");
            // auditService.logTaskUpdate(event.getTask());
            
            log.info("TaskUpdatedEvent processed successfully");
            
        } catch (Exception e) {
            log.error("Failed to process TaskUpdatedEvent", e);
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onStatusChanged(TaskStatusChangedEvent event) {
        log.info("Processing TaskStatusChangedEvent: {} → {}", 
            event.getOldStatus(), 
            event.getNewStatus()
        );
        
        try {
            // Уведомление о смене статуса
            log.debug("Sending status change notification");
            // notificationService.notifyStatusChange(event.getTask());
            
            // Если задача завершена - уведомить всех участников
            if (event.getNewStatus() == TaskStatus.DONE) {
                log.debug("Task completed, notifying stakeholders");
                // notificationService.notifyTaskCompleted(event.getTask());
            }
            
            log.info("TaskStatusChangedEvent processed successfully");
            
        } catch (Exception e) {
            log.error("Failed to process TaskStatusChangedEvent", e);
        }
    }
    
    @Async("jiraEventExecutor")
    @EventListener
    public void onProjectSynced(JiraProjectSyncedEvent event) {
        log.info("Processing JiraProjectSyncedEvent: project={}, tasks={}", 
            event.getProjectId(), 
            event.getTasksCount()
        );
        
        try {
            // Обновить метрики синхронизации
            log.debug("Updating sync metrics");
            // metricsService.recordSyncCompleted(event.getProjectId(), event.getTasksCount());
            
            log.info("JiraProjectSyncedEvent processed successfully");
            
        } catch (Exception e) {
            log.error("Failed to process JiraProjectSyncedEvent", e);
        }
    }
}

