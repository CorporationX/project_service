package faang.school.projectservice.integration.jira.event;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
   
    public void publishTaskCreated(Task task, String jiraIssueKey) {
        log.debug("Publishing TaskCreatedEvent: taskId={}, jiraKey={}", task.getId(), jiraIssueKey);
        eventPublisher.publishEvent(new TaskCreatedEvent(this, task, jiraIssueKey));
    }
    
    public void publishTaskUpdated(Task task, Task oldTask, String jiraIssueKey) {
        log.debug("Publishing TaskUpdatedEvent: taskId={}, jiraKey={}", task.getId(), jiraIssueKey);
        eventPublisher.publishEvent(new TaskUpdatedEvent(this, task, jiraIssueKey));
        
        // Если изменился статус, публикуем отдельное событие
        if (oldTask != null && oldTask.getStatus() != task.getStatus()) {
            publishTaskStatusChanged(task, oldTask.getStatus(), task.getStatus());
        }
    }
   
    public void publishTaskStatusChanged(Task task, TaskStatus oldStatus, TaskStatus newStatus) {
        log.debug("Publishing TaskStatusChangedEvent: taskId={}, {} -> {}", 
            task.getId(), oldStatus, newStatus);
        eventPublisher.publishEvent(new TaskStatusChangedEvent(this, task, oldStatus, newStatus));
    }
   
    public void publishTaskDeleted(Long taskId, String jiraIssueKey) {
        log.debug("Publishing TaskDeletedEvent: taskId={}, jiraKey={}", taskId, jiraIssueKey);
        eventPublisher.publishEvent(new TaskDeletedEvent(this, taskId, jiraIssueKey));
    }
    
    public void publishProjectSynced(Long projectId, int tasksCount) {
        log.debug("Publishing JiraProjectSyncedEvent: projectId={}, tasksCount={}", 
            projectId, tasksCount);
        eventPublisher.publishEvent(new JiraProjectSyncedEvent(this, projectId, tasksCount));
    }
}

