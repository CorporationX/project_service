package faang.school.projectservice.integration.jira.queue;

import com.rabbitmq.client.Channel;
import faang.school.projectservice.integration.jira.event.JiraEventPublisher;
import faang.school.projectservice.integration.jira.queue.config.RabbitMQConfig;
import faang.school.projectservice.integration.jira.service.JiraIntegrationService;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraTaskConsumer {
    
    private final JiraIntegrationService jiraIntegrationService;
    private final TaskRepository taskRepository;
    private final JiraEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;
    
    private static final int MAX_RETRIES = 3;
    
    // ==========================================
    // CREATE Operations
    // ==========================================
    
    @RabbitListener(queues = RabbitMQConfig.TASK_CREATE_QUEUE)
    public void handleTaskCreation(
        @Payload JiraTaskMessage message,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
        Channel channel
    ) throws IOException {
        log.info("Processing CREATE task: taskId={}, correlationId={}, retry={}", 
            message.getTaskId(), 
            message.getCorrelationId(),
            message.getRetryCount()
        );
        
        try {
            Task task = taskRepository.findById(message.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Task not found: " + message.getTaskId()
                ));
            
            String jiraIssueKey;
            if (message.getUserId() != null) {
                jiraIssueKey = jiraIntegrationService.createTaskAsUser(
                    message.getUserId(), 
                    task
                );
            } else {
                jiraIssueKey = jiraIntegrationService.createTaskAsSystem(task);
            }
            
            task.setJiraIssueKey(jiraIssueKey);
            taskRepository.save(task);
            
            eventPublisher.publishTaskCreated(task, jiraIssueKey);
            
            channel.basicAck(tag, false);
            
            log.info("Task created in Jira: taskId={}, jiraKey={}", 
                task.getId(), jiraIssueKey);
            
        } catch (Exception e) {
            handleError(message, tag, channel, e);
        }
    }
    
    // ==========================================
    // UPDATE Operations
    // ==========================================
    
    @RabbitListener(queues = RabbitMQConfig.TASK_UPDATE_QUEUE)
    public void handleTaskUpdate(
        @Payload JiraTaskMessage message,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
        Channel channel
    ) throws IOException {
        log.info("Processing UPDATE task: taskId={}, correlationId={}", 
            message.getTaskId(), 
            message.getCorrelationId()
        );
        
        try {
            Task task = taskRepository.findById(message.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Task not found: " + message.getTaskId()
                ));
            
            if (task.getJiraIssueKey() == null) {
                log.warn("Task {} has no Jira key, skipping update", task.getId());
                channel.basicAck(tag, false);
                return;
            }
            
            Task oldTask = Task.builder()
                .id(task.getId())
                .status(task.getStatus())
                .build();
            
            if (message.getUserId() != null) {
                jiraIntegrationService.updateTaskAsUser(
                    message.getUserId(), 
                    task.getJiraIssueKey(),
                    task
                );
            } else {
                jiraIntegrationService.updateTaskAsSystem(task.getJiraIssueKey(), task);
            }
            
            eventPublisher.publishTaskUpdated(task, oldTask, task.getJiraIssueKey());
            
            channel.basicAck(tag, false);
            
            log.info("Task updated in Jira: taskId={}, jiraKey={}", 
                task.getId(), task.getJiraIssueKey());
            
        } catch (Exception e) {
            handleError(message, tag, channel, e);
        }
    }
    
    // ==========================================
    // DELETE Operations
    // ==========================================
    
    @RabbitListener(queues = RabbitMQConfig.TASK_DELETE_QUEUE)
    public void handleTaskDeletion(
        @Payload JiraTaskMessage message,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
        Channel channel
    ) throws IOException {
        log.info("Processing DELETE task: taskId={}, jiraKey={}", 
            message.getTaskId(), 
            message.getPayload() != null ? message.getPayload().getJiraIssueKey() : null
        );
        
        try {
            String jiraIssueKey = message.getPayload() != null ? 
                message.getPayload().getJiraIssueKey() : null;
            
            if (jiraIssueKey == null) {
                log.warn("No Jira key for task {}, skipping deletion", message.getTaskId());
                channel.basicAck(tag, false);
                return;
            }
            
            if (message.getUserId() != null) {
                jiraIntegrationService.deleteTaskAsUser(message.getUserId(), jiraIssueKey);
            } else {
                jiraIntegrationService.deleteTaskAsSystem(jiraIssueKey);
            }
            
            eventPublisher.publishTaskDeleted(message.getTaskId(), jiraIssueKey);
            
            channel.basicAck(tag, false);
            
            log.info("Task deleted from Jira: jiraKey={}", jiraIssueKey);
            
        } catch (Exception e) {
            handleError(message, tag, channel, e);
        }
    }
    
    // ==========================================
    // SYNC Operations
    // ==========================================
    
    @RabbitListener(queues = RabbitMQConfig.TASK_SYNC_QUEUE)
    public void handleProjectSync(
        @Payload JiraTaskMessage message,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
        Channel channel
    ) throws IOException {
        log.info("Processing SYNC project");
        
        try {
            Long projectId = message.getPayload() != null && message.getPayload().getProject() != null ?
                message.getPayload().getProject().getId() : null;
            
            if (projectId == null) {
                log.warn("No projectId in sync message, skipping");
                channel.basicAck(tag, false);
                return;
            }
            
            var syncResult = jiraIntegrationService.syncProject(projectId);
            
            eventPublisher.publishProjectSynced(
                projectId,
                syncResult.getTotalTasks()
            );
            
            channel.basicAck(tag, false);
            
            log.info("Project synced: projectId={}, total={}, created={}, updated={}, failed={}", 
                projectId,
                syncResult.getTotalTasks(),
                syncResult.getCreatedTasks(),
                syncResult.getUpdatedTasks(),
                syncResult.getFailedTasks()
            );
            
        } catch (Exception e) {
            handleError(message, tag, channel, e);
        }
    }
    
    // ==========================================
    // BULK Operations
    // ==========================================
    
    @RabbitListener(queues = RabbitMQConfig.TASK_BULK_QUEUE)
    public void handleBulkUpdate(
        @Payload JiraTaskMessage message,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
        Channel channel
    ) throws IOException {
        log.info("Processing BULK_UPDATE task: taskId={}", message.getTaskId());
        
        try {
            handleTaskUpdate(message, tag, channel);
            
        } catch (Exception e) {
            handleError(message, tag, channel, e);
        }
    }
    
    // ==========================================
    // Error Handling
    // ==========================================
    
    private void handleError(
        JiraTaskMessage message, 
        long tag, 
        Channel channel, 
        Exception e
    ) throws IOException {
        log.error("Error processing message: {}", message, e);
        
        message.setRetryCount(message.getRetryCount() + 1);
        
        if (message.getRetryCount() < MAX_RETRIES) {
            log.warn("Retrying message (attempt {}/{}): {}", 
                message.getRetryCount(), MAX_RETRIES, message.getCorrelationId());
            
            channel.basicNack(tag, false, true);
            
        } else {
            log.error("Max retries reached, sending to DLQ: {}", message.getCorrelationId());
            
            channel.basicNack(tag, false, false);
        }
    }
    
    // ==========================================
    // DLQ Handlers
    // ==========================================
    
    @RabbitListener(queues = {
        RabbitMQConfig.TASK_CREATE_DLQ,
        RabbitMQConfig.TASK_UPDATE_DLQ,
        RabbitMQConfig.TASK_DELETE_DLQ,
        RabbitMQConfig.TASK_SYNC_DLQ
    })
    public void handleDeadLetterMessage(
        @Payload JiraTaskMessage message,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
        Channel channel
    ) throws IOException {
        log.error("Dead Letter message received: {}", message);
        
        channel.basicAck(tag, false);
    }
}
