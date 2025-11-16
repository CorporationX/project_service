package faang.school.projectservice.integration.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import faang.school.projectservice.integration.jira.JiraSystemClient;
import faang.school.projectservice.integration.jira.client.JiraOauthClient;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueLinkRequest;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueRequest;
import faang.school.projectservice.integration.jira.dto.response.JiraTransitionsResponse;
import faang.school.projectservice.integration.jira.event.JiraEventPublisher;
import faang.school.projectservice.integration.jira.exception.JiraApiException;
import faang.school.projectservice.integration.jira.exception.JiraIntegrationException;
import faang.school.projectservice.integration.jira.exception.JiraOauthException;
import faang.school.projectservice.integration.jira.mapper.JiraMapper;
import faang.school.projectservice.integration.jira.dto.response.OauthTokenResponse;
import faang.school.projectservice.integration.jira.metrics.JiraMetricsService;
import faang.school.projectservice.integration.jira.Oauth.JiraOauthTokenManager;
import faang.school.projectservice.integration.jira.Oauth.model.JiraOauthTokenRepository;
import faang.school.projectservice.integration.jira.Oauth.model.UserJiraOauthToken;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import io.micrometer.core.instrument.Timer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * FACADE для интеграции с Jira
 * 
 * Основная точка входа для работы с Jira API.
 * Решает какой клиент использовать (Oauth или System).
 * Реализует fallback логику.
 */
@Slf4j
@Service
@Data
@RequiredArgsConstructor
public class JiraIntegrationService {
    
    private final JiraOauthClient OauthClient;
    private final JiraSystemClient systemClient;
    private final JiraOauthTokenManager tokenManager;
    private final JiraOauthTokenRepository tokenRepository;
    private final JiraOauthService OauthService;
    private final JiraMapper mapper;
    private final TaskRepository taskRepository;
    private final JiraEventPublisher eventPublisher;
    private final JiraMetricsService metricsService;
    
    // ==========================================
    // CREATE OPERATIONS
    // ==========================================
  
    @Transactional
    public String createTaskAsUser(Long userId, Task task) {
        log.info("Creating task for user: {}, taskId: {}", userId, task.getId());
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        
        if (userId == null) {
            log.warn("UserId is null, using system client");
            metricsService.recordApiRequestDuration(sample, "create_task_system");
            return createTaskAsSystem(task);
        }
        
        try {
        
            String accessToken = tokenManager.getValidToken(userId);
            JiraIssueRequest request = mapper.toJiraRequest(task);
            
            String issueKey = OauthClient.createIssue(accessToken, request);
            log.info("Task created as user via Oauth: {}", issueKey);
            
            task.setJiraIssueKey(issueKey);
            taskRepository.save(task);
   
            eventPublisher.publishTaskCreated(task, issueKey);
            
            metricsService.recordTaskCreated("Oauth");
            metricsService.recordApiRequestDuration(sample, "create_task_Oauth");
            
            return issueKey;
            
        } catch (JiraOauthException e) {
            log.warn("Oauth failed for user {}: {}. Falling back to system client.", userId, e.getMessage());
            metricsService.recordOauthFallback();
            metricsService.recordApiError("Oauth_exception");
            metricsService.recordApiRequestDuration(sample, "create_task_fallback");
            return createTaskAsSystem(task);
            
        } catch (JiraApiException e) {
            if (e.getMessage().contains("token invalid") || e.getMessage().contains("401")) {
                log.warn("Oauth token invalid for user {}. Falling back to system client.", userId);
                metricsService.recordOauthFallback();
                metricsService.recordApiError("invalid_token");
                metricsService.recordApiRequestDuration(sample, "create_task_fallback");
                return createTaskAsSystem(task);
            }
            metricsService.recordTaskFailed("create", e.getClass().getSimpleName());
            metricsService.recordApiError("api_exception");
            metricsService.recordApiRequestDuration(sample, "create_task_error");
            throw e;
        }
    }
    
    @Transactional
    public String createTaskAsSystem(Task task) {
        log.info("Creating task via system client: taskId={}", task.getId());
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        
        try {
            IssueInput issueInput = mapper.toIssueInput(task);
            String issueKey = systemClient.createIssue(issueInput);
            
            task.setJiraIssueKey(issueKey);
            taskRepository.save(task);
           
            eventPublisher.publishTaskCreated(task, issueKey);
            
            metricsService.recordTaskCreated("system");
            metricsService.recordApiRequestDuration(sample, "create_task_system");
            
            log.info("Task created via system client: {}", issueKey);
            return issueKey;
        } catch (Exception e) {
            metricsService.recordTaskFailed("create", e.getClass().getSimpleName());
            metricsService.recordApiError("system_exception");
            metricsService.recordApiRequestDuration(sample, "create_task_error");
            throw e;
        }
    }
    
    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================
    
    @Transactional
    public void updateTaskAsUser(Long userId, String issueKey, Task updates) {
        log.info("Updating task {} for user: {}", issueKey, userId);
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        
        if (userId == null) {
            log.warn("UserId is null, using system client");
            metricsService.recordApiRequestDuration(sample, "update_task_system");
            updateTaskAsSystem(issueKey, updates);
            return;
        }
        
        try {
            // Get old task state for event publishing
            Task oldTask = taskRepository.findByJiraIssueKey(issueKey)
                .map(task -> Task.builder()
                    .id(task.getId())
                    .status(task.getStatus())
                    .build())
                .orElse(null);
           
            String accessToken = tokenManager.getValidToken(userId);
            JiraIssueRequest request = mapper.toJiraRequest(updates);
            
            OauthClient.updateIssue(accessToken, issueKey, request);
            
            if (updates.getStatus() != null) {
                updateStatusAsUser(userId, issueKey, updates.getStatus());
            }
            
            // Get updated task for event publishing
            Task updatedTask = taskRepository.findByJiraIssueKey(issueKey).orElse(updates);
            eventPublisher.publishTaskUpdated(updatedTask, oldTask, issueKey);
            
            metricsService.recordTaskUpdated("Oauth");
            metricsService.recordApiRequestDuration(sample, "update_task_Oauth");
            
            log.info("Task updated as user via Oauth: {}", issueKey);
            
        } catch (JiraOauthException | JiraApiException e) {
            log.warn("Oauth failed for user {}: {}. Falling back to system client.", userId, e.getMessage());
            metricsService.recordOauthFallback();
            metricsService.recordApiError("Oauth_exception");
            metricsService.recordApiRequestDuration(sample, "update_task_fallback");
            updateTaskAsSystem(issueKey, updates);
        } catch (Exception e) {
            metricsService.recordTaskFailed("update", e.getClass().getSimpleName());
            metricsService.recordApiError("api_exception");
            metricsService.recordApiRequestDuration(sample, "update_task_error");
            throw e;
        }
    }
    
    @Transactional
    public void updateTaskAsSystem(String issueKey, Task updates) {
        log.info("Updating task via system client: {}", issueKey);
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        
        try {
            // Get old task state for event publishing
            Task oldTask = taskRepository.findByJiraIssueKey(issueKey)
                .map(task -> Task.builder()
                    .id(task.getId())
                    .status(task.getStatus())
                    .build())
                .orElse(null);
            
            IssueInput issueInput = mapper.toIssueInput(updates);
            systemClient.updateIssue(issueKey, issueInput);
         
            if (updates.getStatus() != null) {
                updateStatusAsSystem(issueKey, updates.getStatus());
            }
            
            // Get updated task for event publishing
            Task updatedTask = taskRepository.findByJiraIssueKey(issueKey).orElse(updates);
            eventPublisher.publishTaskUpdated(updatedTask, oldTask, issueKey);
            
            metricsService.recordTaskUpdated("system");
            metricsService.recordApiRequestDuration(sample, "update_task_system");
            
            log.info("Task updated via system client: {}", issueKey);
        } catch (Exception e) {
            metricsService.recordTaskFailed("update", e.getClass().getSimpleName());
            metricsService.recordApiError("system_exception");
            metricsService.recordApiRequestDuration(sample, "update_task_error");
            throw e;
        }
    }
    
    // ==========================================
    // STATUS CHANGE (TRANSITIONS)
    // ==========================================
    
 
    private void updateStatusAsUser(Long userId, String issueKey, TaskStatus targetStatus) {
        log.debug("Updating status for issue {} to {}", issueKey, targetStatus);
        
        try {
            String accessToken = tokenManager.getValidToken(userId);
            
            JiraTransitionsResponse transitions = OauthClient.getTransitions(accessToken, issueKey);
       
            String targetJiraStatus = mapper.toJiraStatus(targetStatus);
            String transitionId = findTransitionId(transitions, targetJiraStatus);
    
            OauthClient.performTransition(accessToken, issueKey, transitionId);
            
            log.debug("Status updated to {} for issue: {}", targetStatus, issueKey);
            
        } catch (Exception e) {
            log.warn("Failed to update status via Oauth, falling back to system client", e);
            updateStatusAsSystem(issueKey, targetStatus);
        }
    }
    
    private void updateStatusAsSystem(String issueKey, TaskStatus targetStatus) {
        log.debug("Updating status via system client for issue {} to {}", issueKey, targetStatus);
        
        Iterable<Transition> transitions = systemClient.getTransitions(issueKey);
        
        String transitionName = mapper.getTransitionName(targetStatus);
        if (transitionName == null) {
            log.warn("No transition name mapping for status: {}, skipping status update", targetStatus);
            return;
        }
        
        Transition targetTransition = StreamSupport.stream(transitions.spliterator(), false)
            .filter(t -> t.getName().equalsIgnoreCase(transitionName))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "No transition available with name: " + transitionName
            ));
        
        TransitionInput transitionInput = new TransitionInput(targetTransition.getId());
        systemClient.performTransition(issueKey, transitionInput);
        
        log.debug("Status updated to {} via system client", targetStatus);
    }
    
    private String findTransitionId(JiraTransitionsResponse transitions, String targetStatus) {
        return transitions.getTransitions().stream()
            .filter(t -> t.getTo().getName().equalsIgnoreCase(targetStatus))
            .map(JiraTransitionsResponse.Transition::getId)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "No transition available to status: " + targetStatus
            ));
    }
    
    // ==========================================
    // READ OPERATIONS
    // ==========================================
    
   
    public Task getTask(String issueKey) {
        log.debug("Fetching task: {}", issueKey);
        
        Issue issue = systemClient.getIssue(issueKey);
        Task task = mapper.toTask(issue);
        
        log.debug("Task fetched: {}", issueKey);
        return task;
    }
 
    public List<Task> getAllProjectTasks(Long projectId) {
        log.debug("Fetching all tasks for project: {}", projectId);
        
        String jql = "project = " + projectId;
        return searchTasks(jql);
    }
    
    public List<Task> getProjectTasks(
        Long projectId,
        TaskStatus status,
        String assignee
    ) {
        log.debug("Fetching tasks for project: {} with filters", projectId);
        
        StringBuilder jql = new StringBuilder("project = " + projectId);
        
        if (status != null) {
            String jiraStatus = mapper.toJiraStatus(status);
            jql.append(" AND status = '").append(jiraStatus).append("'");
        }
        
        if (assignee != null && !assignee.isBlank()) {
            jql.append(" AND assignee = '").append(assignee).append("'");
        }
        
        log.debug("JQL query: {}", jql);
        return searchTasks(jql.toString());
    }
    
    private List<Task> searchTasks(String jql) {
        List<Issue> issues = systemClient.searchIssues(jql, 100);
        
        List<Task> tasks = issues.stream()
            .map(mapper::toTask)
            .collect(Collectors.toList());
        
        log.debug("Found {} tasks", tasks.size());
        return tasks;
    }
    
    // ==========================================
    // LINK OPERATIONS
    // ==========================================
    
    public void linkTasksAsUser(Long userId, String sourceKey, String targetKey, String linkType) {
        log.info("Linking tasks: {} -> {} (type: {})", sourceKey, targetKey, linkType);
        
        if (userId == null) {
            linkTasksAsSystem(sourceKey, targetKey, linkType);
            return;
        }
        
        try {
            String accessToken = tokenManager.getValidToken(userId);
            
            JiraIssueLinkRequest request = JiraIssueLinkRequest.builder()
                .type(JiraIssueLinkRequest.LinkType.builder()
                    .name(linkType)
                    .build())
                .inwardIssue(JiraIssueLinkRequest.InwardIssue.builder()
                    .key(sourceKey)
                    .build())
                .outwardIssue(JiraIssueLinkRequest.OutwardIssue.builder()
                    .key(targetKey)
                    .build())
                .build();
            
            OauthClient.linkIssues(accessToken, request);
            log.info("Tasks linked via Oauth");
            
        } catch (Exception e) {
            log.warn("Failed to link via Oauth, falling back to system client", e);
            linkTasksAsSystem(sourceKey, targetKey, linkType);
        }
    }
    
    public void linkTasksAsSystem(String sourceKey, String targetKey, String linkType) {
        log.info("Linking tasks via system client: {} -> {}", sourceKey, targetKey);
        
        systemClient.linkIssues(sourceKey, targetKey, linkType);
        
        log.info("Tasks linked via system client");
    }
    
    // ==========================================
    // DELETE Operations
    // ==========================================
    
    @Transactional
    public void deleteTaskAsUser(Long userId, String jiraIssueKey) {
        log.info("Deleting task from Jira as user: userId={}, jiraKey={}", userId, jiraIssueKey);
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        
        try {
            if (!hasValidOauthToken(userId)) {
                metricsService.recordApiRequestDuration(sample, "delete_task_system");
                deleteTaskAsSystem(jiraIssueKey);
                return;
            }
            
            deleteTaskAsSystem(jiraIssueKey);
            metricsService.recordTaskDeleted("Oauth");
            metricsService.recordApiRequestDuration(sample, "delete_task_Oauth");
            
        } catch (Exception e) {
            log.error("Failed to delete task", e);
            metricsService.recordTaskFailed("delete", e.getClass().getSimpleName());
            metricsService.recordApiError("delete_exception");
            metricsService.recordApiRequestDuration(sample, "delete_task_error");
            throw new JiraIntegrationException("Failed to delete task from Jira", e);
        }
    }
    
    @Transactional
    public void deleteTaskAsSystem(String jiraIssueKey) {
        log.info("Deleting task from Jira as system: jiraKey={}", jiraIssueKey);
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        
        try {
            systemClient.deleteIssue(jiraIssueKey, false);
            
            taskRepository.findByJiraIssueKey(jiraIssueKey)
                .ifPresent(task -> {
                    eventPublisher.publishTaskDeleted(task.getId(), jiraIssueKey);
                });
            
            metricsService.recordTaskDeleted("system");
            metricsService.recordApiRequestDuration(sample, "delete_task_system");
            
            log.info("Task deleted from Jira: jiraKey={}", jiraIssueKey);
            
        } catch (Exception e) {
            log.error("Failed to delete task from Jira", e);
            metricsService.recordTaskFailed("delete", e.getClass().getSimpleName());
            metricsService.recordApiError("system_exception");
            metricsService.recordApiRequestDuration(sample, "delete_task_error");
            throw new JiraIntegrationException("Failed to delete task from Jira", e);
        }
    }
    
    // ==========================================
    // SYNC Operations
    // ==========================================
    
    @Transactional
    public SyncResult syncProject(Long projectId) {
        log.info("Starting sync for project: {}", projectId);
        
        Timer.Sample sample = metricsService.startApiRequestTimer();
        SyncResult result = new SyncResult();
        
        try {
            List<Issue> jiraIssues = systemClient.searchIssues(
                "project = " + projectId, 
                1000
            );
            result.setTotalTasks(jiraIssues.size());
            
            List<Task> ourTasks = taskRepository.findAllByProjectId(projectId);
            
            for (Issue jiraIssue : jiraIssues) {
                try {
                    Optional<Task> existingTask = ourTasks.stream()
                        .filter(t -> jiraIssue.getKey().equals(t.getJiraIssueKey()))
                        .findFirst();
                    
                    if (existingTask.isPresent()) {
                        Task task = existingTask.get();
                        updateTaskFromJira(task, jiraIssue);
                        taskRepository.save(task);
                        result.incrementUpdated();
                    } else {
                        Task newTask = mapper.toTask(jiraIssue);
                        if (newTask.getProject() == null) {
                            Project project = new Project();
                            project.setId(projectId);
                            newTask.setProject(project);
                        }
                        taskRepository.save(newTask);
                        result.incrementCreated();
                    }
                    
                } catch (Exception e) {
                    log.error("Failed to sync issue: {}", jiraIssue.getKey(), e);
                    result.incrementFailed();
                    metricsService.recordTaskFailed("sync", e.getClass().getSimpleName());
                }
            }
            
            eventPublisher.publishProjectSynced(
                projectId,
                result.getTotalTasks()
            );
            
            metricsService.recordTaskSynced(result.getCreatedTasks() + result.getUpdatedTasks());
            metricsService.recordApiRequestDuration(sample, "sync_project");
            
            log.info("Sync completed for project {}: {}", projectId, result);
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to sync project", e);
            metricsService.recordTaskFailed("sync", e.getClass().getSimpleName());
            metricsService.recordApiError("sync_exception");
            metricsService.recordApiRequestDuration(sample, "sync_project_error");
            throw new JiraIntegrationException("Failed to sync project: " + projectId, e);
        }
    }
    
    // ==========================================
    // Oauth Token Management
    // ==========================================
    
    @Transactional
    public void refreshOauthToken(Long userId) {
        log.debug("Refreshing Oauth token for user: {}", userId);
        
        try {
            UserJiraOauthToken token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new JiraIntegrationException("No token found for user: " + userId));
            
            OauthTokenResponse response = OauthService.refreshAccessToken(token.getRefreshToken());
            
            token.setAccessToken(response.getAccessToken());
            if (response.getRefreshToken() != null) {
                token.setRefreshToken(response.getRefreshToken());
            }
            token.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()));
            tokenRepository.save(token);
            
            log.info("Oauth token refreshed for user: {}", userId);
            
        } catch (Exception e) {
            log.error("Failed to refresh Oauth token", e);
            throw new JiraIntegrationException("Failed to refresh token for user: " + userId, e);
        }
    }
    
    // ==========================================
    // Health & Status
    // ==========================================
    
    public boolean checkJiraHealth() {
        try {
            systemClient.getIssue("TEST-1");
            return true;
        } catch (Exception e) {
            log.warn("Jira health check failed", e);
            return false;
        }
    }
    
    public IntegrationStatus getIntegrationStatus() {
        IntegrationStatus status = new IntegrationStatus();
        
        status.setSystemClientAvailable(checkJiraHealth());
        
        long activeTokens = tokenRepository.countActiveTokens(LocalDateTime.now());
        status.setActiveOauthTokens(activeTokens);
        
        long unsyncedTasks = taskRepository.findAll().stream()
            .filter(t -> t.getJiraIssueKey() == null)
            .count();
        status.setUnsyncedTasks(unsyncedTasks);
        
        return status;
    }
    
    // ==========================================
    // UTILITY
    // ==========================================
    
    private boolean hasValidOauthToken(Long userId) {
        return tokenRepository.findByUserId(userId)
            .map(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
            .orElse(false);
    }
    
    private void updateTaskFromJira(Task task, Issue jiraIssue) {
        task.setName(jiraIssue.getSummary());
        task.setDescription(jiraIssue.getDescription());
        task.setStatus(mapper.fromJiraStatus(jiraIssue.getStatus().getName()));
        
        if (jiraIssue.getAssignee() != null) {
            // TODO: Получить userId по email из UserService
        }
        
        if (jiraIssue.getDueDate() != null) {
            // TODO: Добавить поле deadline в Task если нужно
        }
    }
    
    // ==========================================
    // Inner Classes
    // ==========================================
    
    @Data
    public static class SyncResult {
        private int totalTasks;
        private int createdTasks;
        private int updatedTasks;
        private int failedTasks;
        
        public void incrementCreated() {
            createdTasks++;
        }
        
        public void incrementUpdated() {
            updatedTasks++;
        }
        
        public void incrementFailed() {
            failedTasks++;
        }
    }
    
    @Data
    public static class IntegrationStatus {
        private boolean systemClientAvailable;
        private long activeOauthTokens;
        private long unsyncedTasks;
    }
}

