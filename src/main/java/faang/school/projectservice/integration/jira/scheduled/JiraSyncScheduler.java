package faang.school.projectservice.integration.jira.scheduled;

import faang.school.projectservice.integration.jira.websocket.JiraWebSocketService;

import faang.school.projectservice.integration.jira.event.JiraEventPublisher;
import faang.school.projectservice.integration.jira.oauth.model.JiraOAuthTokenRepository;
import faang.school.projectservice.integration.jira.oauth.model.UserJiraOAuthToken;
import faang.school.projectservice.integration.jira.queue.JiraTaskQueue;
import faang.school.projectservice.integration.jira.service.JiraIntegrationService;
import faang.school.projectservice.integration.jira.service.JiraOAuthService;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "jira.scheduled.enabled", havingValue = "true", matchIfMissing = true)
public class JiraSyncScheduler {
    
    private final JiraIntegrationService jiraIntegrationService;
    private final TaskRepository taskRepository;
    private final JiraOAuthTokenRepository tokenRepository;
    private final JiraTaskQueue queueProducer;
    private final JiraEventPublisher eventPublisher;
    private final JiraOAuthService oauthService;
    private final JiraWebSocketService webSocketService;
    
    // ==========================================
    // Project Sync
    // ==========================================
    
    @Scheduled(fixedDelayString = "${jira.sync.interval:900000}")
    @Transactional
    public void syncProjectsWithJira() {
        log.info("Starting scheduled Jira sync...");
        
        try {
            List<Long> projectIds = taskRepository.findDistinctProjectIds();
            
            if (projectIds.isEmpty()) {
                log.info("No projects to sync");
                return;
            }
            
            AtomicInteger totalSynced = new AtomicInteger(0);
            AtomicInteger totalFailed = new AtomicInteger(0);
            
            projectIds.forEach(projectId -> {
                try {
                    log.debug("Syncing project: {}", projectId);
                    
                    queueProducer.queueProjectSync(projectId, null);
                    
                    totalSynced.incrementAndGet();
                    
                } catch (Exception e) {
                    log.error("Failed to queue sync for project: {}", projectId, e);
                    totalFailed.incrementAndGet();
                }
            });
            
            log.info("Jira sync completed: {} projects queued, {} failed", 
                totalSynced.get(), totalFailed.get());
            
        } catch (Exception e) {
            log.error("Failed to execute scheduled Jira sync", e);
        }
    }
    
    // ==========================================
    // Token Refresh
    // ==========================================
    
    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void refreshExpiringTokens() {
        log.info("Checking OAuth tokens for refresh...");
        
        try {
            LocalDateTime expiryThreshold = LocalDateTime.now().plusHours(2);
            List<UserJiraOAuthToken> expiringTokens = 
                tokenRepository.findTokensExpiringBefore(LocalDateTime.now(), expiryThreshold);
            
            if (expiringTokens.isEmpty()) {
                log.info("No tokens require refresh");
                return;
            }
            
            log.info("Found {} tokens that need refresh", expiringTokens.size());
            
            AtomicInteger refreshed = new AtomicInteger(0);
            AtomicInteger failed = new AtomicInteger(0);
            
            expiringTokens.forEach(token -> {
                try {
                    log.debug("Refreshing token for user: {}", token.getUserId());
                    
                    jiraIntegrationService.refreshOAuthToken(token.getUserId());
                    refreshed.incrementAndGet();
                    
                } catch (Exception e) {
                    log.error("Failed to refresh token for user: {}", 
                        token.getUserId(), e);
                    failed.incrementAndGet();
                }
            });
            
            log.info("Token refresh completed: {} refreshed, {} failed", 
                refreshed.get(), failed.get());
            
        } catch (Exception e) {
            log.error("Failed to execute token refresh", e);
        }
    }
    
    // ==========================================
    // Unsynced Tasks
    // ==========================================
    
    @Scheduled(fixedDelay = 1800000)
    @Transactional
    public void syncUnsyncedTasks() {
        log.info("Checking for unsynced tasks...");
        
        try {
            List<Task> unsyncedTasks = taskRepository.findAll().stream()
                .filter(t -> t.getJiraIssueKey() == null)
                .toList();
            
            if (unsyncedTasks.isEmpty()) {
                log.info("All tasks are synced with Jira");
                return;
            }
            
            log.info("Found {} unsynced tasks", unsyncedTasks.size());
            
            AtomicInteger created = new AtomicInteger(0);
            AtomicInteger failed = new AtomicInteger(0);
            
            unsyncedTasks.forEach(task -> {
                try {
                    log.debug("Creating Jira issue for task: {}", task.getId());
                    
                    queueProducer.queueTaskCreation(task, task.getReporterUserId());
                    created.incrementAndGet();
                    
                } catch (Exception e) {
                    log.error("Failed to queue task creation: {}", task.getId(), e);
                    failed.incrementAndGet();
                }
            });
            
            log.info("Unsynced tasks processing: {} queued, {} failed", 
                created.get(), failed.get());
            
        } catch (Exception e) {
            log.error("Failed to process unsynced tasks", e);
        }
    }
    
    // ==========================================
    // Cleanup
    // ==========================================
    
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired OAuth tokens...");
        
        try {
            LocalDateTime cleanupThreshold = LocalDateTime.now().minusWeeks(1);
            List<UserJiraOAuthToken> expiredTokens = tokenRepository
                .findExpiredTokens(cleanupThreshold);
            
            int deleted = expiredTokens.size();
            tokenRepository.deleteAll(expiredTokens);
            
            log.info("Deleted {} expired OAuth tokens", deleted);
            
        } catch (Exception e) {
            log.error("Failed to cleanup expired tokens", e);
        }
    }
    
    // ==========================================
    // Health Check
    // ==========================================
    
    @Scheduled(fixedDelay = 300000)
    public void checkJiraHealth() {
        log.debug("Checking Jira health...");
        
        try {
            boolean isHealthy = jiraIntegrationService.checkJiraHealth();
            
            if (isHealthy) {
                log.debug("Jira is healthy");
            } else {
                log.warn("Jira health check failed!");
            }
            
        } catch (Exception e) {
            log.error("Failed to check Jira health", e);
        }
    }
    
    // ==========================================
    // Metrics
    // ==========================================
    
    @Scheduled(fixedDelay = 600000)
    public void collectIntegrationMetrics() {
        log.debug("Collecting integration metrics...");
        
        try {
            long totalTasks = taskRepository.count();
            long syncedTasks = taskRepository.findAll().stream()
                .filter(t -> t.getJiraIssueKey() != null)
                .count();
            long unsyncedTasks = taskRepository.findAll().stream()
                .filter(t -> t.getJiraIssueKey() == null)
                .count();
            long activeTokens = tokenRepository.countActiveTokens(LocalDateTime.now());
            
            log.info("Integration metrics: total={}, synced={}, unsynced={}, activeTokens={}", 
                totalTasks, syncedTasks, unsyncedTasks, activeTokens);
            
        } catch (Exception e) {
            log.error("Failed to collect metrics", e);
        }
    }
}
