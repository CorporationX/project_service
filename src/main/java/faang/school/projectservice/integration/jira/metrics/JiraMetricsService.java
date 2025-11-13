package faang.school.projectservice.integration.jira.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для сбора метрик Jira интеграции
 * 
 * Метрики:
 * - jira.tasks.created - количество созданных задач
 * - jira.tasks.updated - количество обновленных задач
 * - jira.tasks.deleted - количество удаленных задач
 * - jira.tasks.synced - количество синхронизированных задач
 * - jira.tasks.failed - количество неудачных операций
 * - jira.api.requests.duration - время выполнения запросов к Jira API
 * - jira.oauth.tokens.active - количество активных OAuth токенов
 * - jira.oauth.fallback - количество fallback на System client
 * - jira.api.errors - количество ошибок API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JiraMetricsService {
    
    private final MeterRegistry meterRegistry;
    
    // ==========================================
    // Counters
    // ==========================================
    
    private Counter getTasksCreatedCounter(String clientType) {
        return Counter.builder("jira.tasks.created")
            .description("Total number of tasks created in Jira")
            .tag("client.type", clientType)
            .register(meterRegistry);
    }
    
    private Counter getTasksUpdatedCounter(String clientType) {
        return Counter.builder("jira.tasks.updated")
            .description("Total number of tasks updated in Jira")
            .tag("client.type", clientType)
            .register(meterRegistry);
    }
    
    private Counter getTasksDeletedCounter(String clientType) {
        return Counter.builder("jira.tasks.deleted")
            .description("Total number of tasks deleted in Jira")
            .tag("client.type", clientType)
            .register(meterRegistry);
    }
    
    private Counter getTasksSyncedCounter() {
        return Counter.builder("jira.tasks.synced")
            .description("Total number of tasks synced from Jira")
            .register(meterRegistry);
    }
    
    private Counter getTasksFailedCounter(String operation, String reason) {
        return Counter.builder("jira.tasks.failed")
            .description("Total number of failed Jira operations")
            .tag("operation", operation)
            .tag("reason", reason)
            .register(meterRegistry);
    }
    
    private Counter getOAuthFallbackCounter() {
        return Counter.builder("jira.oauth.fallback")
            .description("Total number of OAuth fallbacks to System client")
            .register(meterRegistry);
    }
    
    private Counter getApiErrorsCounter(String errorType) {
        return Counter.builder("jira.api.errors")
            .description("Total number of Jira API errors")
            .tag("error.type", errorType)
            .register(meterRegistry);
    }
    
    // ==========================================
    // Timers
    // ==========================================
    
    private Timer getApiRequestTimer(String operation) {
        return Timer.builder("jira.api.requests.duration")
            .description("Duration of Jira API requests")
            .tag("operation", operation)
            .register(meterRegistry);
    }
    
    // ==========================================
    // Public Methods
    // ==========================================
    
    public void recordTaskCreated(String clientType) {
        getTasksCreatedCounter(clientType).increment();
        log.debug("Recorded task created metric: clientType={}", clientType);
    }
    
    public void recordTaskUpdated(String clientType) {
        getTasksUpdatedCounter(clientType).increment();
        log.debug("Recorded task updated metric: clientType={}", clientType);
    }
    
    public void recordTaskDeleted(String clientType) {
        getTasksDeletedCounter(clientType).increment();
        log.debug("Recorded task deleted metric: clientType={}", clientType);
    }
    
    public void recordTaskSynced(int count) {
        getTasksSyncedCounter().increment(count);
        log.debug("Recorded task synced metric: count={}", count);
    }
    
    public void recordTaskFailed(String operation, String reason) {
        getTasksFailedCounter(operation, reason).increment();
        log.debug("Recorded task failed metric: operation={}, reason={}", operation, reason);
    }
    
    public void recordOAuthFallback() {
        getOAuthFallbackCounter().increment();
        log.debug("Recorded OAuth fallback metric");
    }
    
    public void recordApiError(String errorType) {
        getApiErrorsCounter(errorType).increment();
        log.debug("Recorded API error metric: errorType={}", errorType);
    }
    
    public Timer.Sample startApiRequestTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordApiRequestDuration(Timer.Sample sample, String operation) {
        sample.stop(getApiRequestTimer(operation));
        log.debug("Recorded API request duration: operation={}", operation);
    }
    
    public void registerActiveTokensGauge(java.util.function.Supplier<Number> supplier) {
        Gauge.builder("jira.oauth.tokens.active", supplier)
            .description("Number of active OAuth tokens")
            .register(meterRegistry);
    }
    
    public void registerUnsyncedTasksGauge(java.util.function.Supplier<Number> supplier) {
        Gauge.builder("jira.tasks.unsynced", supplier)
            .description("Number of unsynced tasks")
            .register(meterRegistry);
    }
    
    // ==========================================
    // Cache Metrics
    // ==========================================
    
    private Counter getCacheHitCounter(String cacheType) {
        return Counter.builder("jira.cache.hits")
            .description("Number of cache hits")
            .tag("cache.type", cacheType)
            .register(meterRegistry);
    }
    
    private Counter getCacheMissCounter(String cacheType) {
        return Counter.builder("jira.cache.misses")
            .description("Number of cache misses")
            .tag("cache.type", cacheType)
            .register(meterRegistry);
    }
    
    public void recordCacheHit(String cacheType) {
        getCacheHitCounter(cacheType).increment();
        log.debug("Recorded cache hit: cacheType={}", cacheType);
    }
    
    public void recordCacheMiss(String cacheType) {
        getCacheMissCounter(cacheType).increment();
        log.debug("Recorded cache miss: cacheType={}", cacheType);
    }
}

