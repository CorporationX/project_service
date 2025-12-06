package faang.school.projectservice.integration.jira.queue;

import faang.school.projectservice.integration.jira.queue.config.RabbitMQConfig;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraTaskQueue {
    
    private final RabbitTemplate rabbitTemplate;
    
    // ==========================================
    // Task Operations
    // ==========================================
    
    public void queueTaskCreation(Task task, Long userId) {
        JiraTaskMessage message = JiraTaskMessage.builder()
            .taskId(task.getId())
            .userId(userId)
            .operation(JiraTaskMessage.OperationType.CREATE)
            .payload(task)
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendMessage(RabbitMQConfig.TASK_CREATE_KEY, message);
        log.info("Task queued for creation: taskId={}, correlationId={}", 
            task.getId(), message.getCorrelationId());
    }
    
    public void queueTaskUpdate(Task task, Long userId) {
        JiraTaskMessage message = JiraTaskMessage.builder()
            .taskId(task.getId())
            .userId(userId)
            .operation(JiraTaskMessage.OperationType.UPDATE)
            .payload(task)
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendMessage(RabbitMQConfig.TASK_UPDATE_KEY, message);
        log.info("Task queued for update: taskId={}, correlationId={}", 
            task.getId(), message.getCorrelationId());
    }
    
    public void queueTaskDeletion(Long taskId, String jiraIssueKey, Long userId) {
        Task task = new Task();
        task.setId(taskId);
        task.setJiraIssueKey(jiraIssueKey);
        
        JiraTaskMessage message = JiraTaskMessage.builder()
            .taskId(taskId)
            .userId(userId)
            .operation(JiraTaskMessage.OperationType.DELETE)
            .payload(task)
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendMessage(RabbitMQConfig.TASK_DELETE_KEY, message);
        log.info("Task queued for deletion: taskId={}, jiraKey={}", 
            taskId, jiraIssueKey);
    }
    
    // ==========================================
    // Project Operations
    // ==========================================
    
    public void queueProjectSync(Long projectId, Long userId) {
        // Create a minimal Task with Project to pass projectId in payload
        Project project = new Project();
        project.setId(projectId);
        project.setName("Sync Project"); // Required field
        project.setStatus(ProjectStatus.IN_PROGRESS); // Required field
        project.setVisibility(ProjectVisibility.PUBLIC); // Required field
        
        Task taskPayload = new Task();
        taskPayload.setProject(project);
        
        JiraTaskMessage message = JiraTaskMessage.builder()
            .userId(userId)
            .operation(JiraTaskMessage.OperationType.SYNC)
            .payload(taskPayload)
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        sendMessage(RabbitMQConfig.TASK_SYNC_KEY, message);
        log.info("Project queued for sync: projectId={}", projectId);
    }
    
    // ==========================================
    // Bulk Operations
    // ==========================================
    
    public void queueBulkUpdate(List<Task> tasks, Long userId) {
        for (Task task : tasks) {
            JiraTaskMessage message = JiraTaskMessage.builder()
                .taskId(task.getId())
                .userId(userId)
                .operation(JiraTaskMessage.OperationType.BULK_UPDATE)
                .payload(task)
                .correlationId(UUID.randomUUID().toString())
                .build();
            
            sendMessageWithPriority(RabbitMQConfig.TASK_BULK_KEY, message, 5);
        }
        
        log.info("{} tasks queued for bulk update", tasks.size());
    }
    
    // ==========================================
    // Private Helpers
    // ==========================================
    
    private void sendMessage(String routingKey, JiraTaskMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JIRA_EXCHANGE,
                routingKey,
                message
            );
        } catch (Exception e) {
            log.error("Failed to send message to queue: {}", routingKey, e);
            throw new RuntimeException("Failed to queue task", e);
        }
    }
    
    private void sendMessageWithPriority(String routingKey, JiraTaskMessage message, int priority) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JIRA_EXCHANGE,
                routingKey,
                message,
                m -> {
                    m.getMessageProperties().setPriority(priority);
                    return m;
                }
            );
        } catch (Exception e) {
            log.error("Failed to send priority message to queue: {}", routingKey, e);
            throw new RuntimeException("Failed to queue task with priority", e);
        }
    }
}

