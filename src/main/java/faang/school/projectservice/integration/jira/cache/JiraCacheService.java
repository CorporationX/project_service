package faang.school.projectservice.integration.jira.cache;

import com.atlassian.jira.rest.client.api.domain.Transition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.integration.jira.config.JiraProperties;
import faang.school.projectservice.integration.jira.dto.response.JiraIssueResponse;
import faang.school.projectservice.integration.jira.dto.response.JiraTransitionsResponse;
import faang.school.projectservice.integration.jira.metrics.JiraMetricsService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для кэширования Jira данных в Redis
 * 
 * Кэширует:
 * - Issues (задачи) - по issueKey
 * - Transitions (переходы) - по issueKey
 * - Projects (проекты) - список проектов
 * - Users (пользователи) - список пользователей
 * 
 * TTL настраивается в application.yaml через jira.cache.*
 */
@Slf4j
@Service
public class JiraCacheService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final JiraProperties jiraProperties;
    private final JiraMetricsService metricsService;
    
    public JiraCacheService(
            @Qualifier("jiraCacheRedisTemplate") RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JiraProperties jiraProperties,
            JiraMetricsService metricsService
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.jiraProperties = jiraProperties;
        this.metricsService = metricsService;
    }
    
    private static final String CACHE_PREFIX_ISSUE = "jira:cache:issue:";
    private static final String CACHE_PREFIX_TRANSITIONS = "jira:cache:transitions:";
    private static final String CACHE_PREFIX_PROJECTS = "jira:cache:projects";
    private static final String CACHE_PREFIX_USERS = "jira:cache:users";
    
    // ==========================================
    // Issue Cache (OAuth Client - JiraIssueResponse)
    // ==========================================
    
    /**
     * Получить Issue из кэша (OAuth Client)
     */
    public Optional<JiraIssueResponse> getIssue(String issueKey) {
        if (!isCacheEnabled()) {
            metricsService.recordCacheMiss("issue");
            return Optional.empty();
        }
        
        String key = CACHE_PREFIX_ISSUE + issueKey;
        String cached = redisTemplate.opsForValue().get(key);
        
        if (cached == null) {
            log.debug("Issue not found in cache: {}", issueKey);
            metricsService.recordCacheMiss("issue");
            return Optional.empty();
        }
        
        try {
            JiraIssueResponse issue = objectMapper.readValue(cached, JiraIssueResponse.class);
            log.debug("Issue retrieved from cache: {}", issueKey);
            metricsService.recordCacheHit("issue");
            return Optional.of(issue);
        } catch (Exception e) {
            log.error("Failed to deserialize issue from cache: {}", issueKey, e);
            metricsService.recordCacheMiss("issue");
            return Optional.empty();
        }
    }
    
    /**
     * Сохранить Issue в кэш (OAuth Client)
     */
    public void putIssue(String issueKey, JiraIssueResponse issue) {
        if (!isCacheEnabled()) {
            return;
        }
        
        String key = CACHE_PREFIX_ISSUE + issueKey;
        Duration ttl = getIssueCacheTtl();
        
        try {
            String serialized = objectMapper.writeValueAsString(issue);
            redisTemplate.opsForValue().set(key, serialized, ttl);
            log.debug("Issue cached: {} (TTL: {} seconds)", issueKey, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to serialize issue to cache: {}", issueKey, e);
        }
    }
    
    /**
     * Удалить Issue из кэша
     */
    public void evictIssue(String issueKey) {
        String key = CACHE_PREFIX_ISSUE + issueKey;
        redisTemplate.delete(key);
        log.debug("Issue evicted from cache: {}", issueKey);
    }
    
    // ==========================================
    // Transitions Cache
    // ==========================================
    
    /**
     * Получить Transitions из кэша (OAuth Client)
     */
    public Optional<JiraTransitionsResponse> getTransitions(String issueKey) {
        if (!isCacheEnabled()) {
            metricsService.recordCacheMiss("transitions");
            return Optional.empty();
        }
        
        String key = CACHE_PREFIX_TRANSITIONS + issueKey;
        String cached = redisTemplate.opsForValue().get(key);
        
        if (cached == null) {
            log.debug("Transitions not found in cache: {}", issueKey);
            metricsService.recordCacheMiss("transitions");
            return Optional.empty();
        }
        
        try {
            JiraTransitionsResponse transitions = objectMapper.readValue(cached, JiraTransitionsResponse.class);
            log.debug("Transitions retrieved from cache: {}", issueKey);
            metricsService.recordCacheHit("transitions");
            return Optional.of(transitions);
        } catch (Exception e) {
            log.error("Failed to deserialize transitions from cache: {}", issueKey, e);
            metricsService.recordCacheMiss("transitions");
            return Optional.empty();
        }
    }
    
    /**
     * Сохранить Transitions в кэш (OAuth Client)
     */
    public void putTransitions(String issueKey, JiraTransitionsResponse transitions) {
        if (!isCacheEnabled()) {
            return;
        }
        
        String key = CACHE_PREFIX_TRANSITIONS + issueKey;
        Duration ttl = getTransitionsCacheTtl();
        
        try {
            String serialized = objectMapper.writeValueAsString(transitions);
            redisTemplate.opsForValue().set(key, serialized, ttl);
            log.debug("Transitions cached: {} (TTL: {} seconds)", issueKey, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to serialize transitions to cache: {}", issueKey, e);
        }
    }
    
    /**
     * Сохранить Transitions в кэш (System Client - List&lt;Transition&gt;)
     */
    public void putTransitions(String issueKey, List<Transition> transitions) {
        if (!isCacheEnabled()) {
            return;
        }
        
        String key = CACHE_PREFIX_TRANSITIONS + issueKey;
        Duration ttl = getTransitionsCacheTtl();
        
        try {
            String serialized = objectMapper.writeValueAsString(transitions);
            redisTemplate.opsForValue().set(key, serialized, ttl);
            log.debug("Transitions cached: {} (TTL: {} seconds)", issueKey, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to serialize transitions to cache: {}", issueKey, e);
        }
    }
    
    /**
     * Получить Transitions из кэша (System Client - List&lt;Transition&gt;)
     */
    public Optional<List<Transition>> getTransitionsList(String issueKey) {
        if (!isCacheEnabled()) {
            metricsService.recordCacheMiss("transitions");
            return Optional.empty();
        }
        
        String key = CACHE_PREFIX_TRANSITIONS + issueKey;
        String cached = redisTemplate.opsForValue().get(key);
        
        if (cached == null) {
            log.debug("Transitions not found in cache: {}", issueKey);
            metricsService.recordCacheMiss("transitions");
            return Optional.empty();
        }
        
        try {
            List<Transition> transitions = objectMapper.readValue(
                cached,
                new TypeReference<List<Transition>>() {}
            );
            log.debug("Transitions retrieved from cache: {}", issueKey);
            metricsService.recordCacheHit("transitions");
            return Optional.of(transitions);
        } catch (Exception e) {
            log.error("Failed to deserialize transitions from cache: {}", issueKey, e);
            metricsService.recordCacheMiss("transitions");
            return Optional.empty();
        }
    }
    
    public void evictTransitions(String issueKey) {
        String key = CACHE_PREFIX_TRANSITIONS + issueKey;
        redisTemplate.delete(key);
        log.debug("Transitions evicted from cache: {}", issueKey);
    }
    
    // ==========================================
    // Projects Cache
    // ==========================================
    
    /**
     * Получить список Projects из кэша
     * Примечание: Projects из JRJC не кэшируются напрямую из-за сложности сериализации
     * Для реализации нужно создать DTO для Projects
     */
    public Optional<List<com.atlassian.jira.rest.client.api.domain.Project>> getProjects() {
        return Optional.empty();
    }
    
    /**
     * Сохранить список Projects в кэш
     * Примечание: Projects из JRJC не кэшируются напрямую из-за сложности сериализации
     * Для реализации нужно создать DTO для Projects
     */
    public void putProjects(List<com.atlassian.jira.rest.client.api.domain.Project> projects) {
        // Projects кэширование не реализовано
    }
    
    public void evictProjects() {
        redisTemplate.delete(CACHE_PREFIX_PROJECTS);
        log.debug("Projects evicted from cache");
    }
    
    // ==========================================
    // Cache Management
    // ==========================================
    
    /**
     * Очистить весь кэш Jira
     */
    public void clearCache() {
        Set<String> issueKeys = redisTemplate.keys(CACHE_PREFIX_ISSUE + "*");
        Set<String> transitionKeys = redisTemplate.keys(CACHE_PREFIX_TRANSITIONS + "*");
        
        if (issueKeys != null && !issueKeys.isEmpty()) {
            redisTemplate.delete(issueKeys);
        }
        if (transitionKeys != null && !transitionKeys.isEmpty()) {
            redisTemplate.delete(transitionKeys);
        }
        
        redisTemplate.delete(CACHE_PREFIX_PROJECTS);
        redisTemplate.delete(CACHE_PREFIX_USERS);
        log.info("Jira cache cleared");
    }
    
    /**
     * Инвалидировать кэш для Issue (удалить issue и transitions)
     */
    public void invalidateIssue(String issueKey) {
        evictIssue(issueKey);
        evictTransitions(issueKey);
        log.debug("Issue cache invalidated: {}", issueKey);
    }
    
    // ==========================================
    // Configuration
    // ==========================================
    
    private boolean isCacheEnabled() {
        return jiraProperties.getCache() != null 
            && jiraProperties.getCache().isEnabled();
    }
    
    private Duration getIssueCacheTtl() {
        if (jiraProperties.getCache() == null) {
            return Duration.ofMinutes(30); // Default TTL
        }
        return Duration.ofMinutes(jiraProperties.getCache().getIssueTtlMinutes());
    }
    
    private Duration getTransitionsCacheTtl() {
        if (jiraProperties.getCache() == null) {
            return Duration.ofMinutes(15); // Default TTL
        }
        return Duration.ofMinutes(jiraProperties.getCache().getTransitionsTtlMinutes());
    }
    
    private Duration getProjectsCacheTtl() {
        if (jiraProperties.getCache() == null) {
            return Duration.ofHours(24); // Default TTL
        }
        return Duration.ofHours(jiraProperties.getCache().getProjectsTtlHours());
    }
    
    // ==========================================
    // Cache Statistics
    // ==========================================
    
    public CacheStatistics getStatistics() {
        Set<String> issueKeys = redisTemplate.keys(CACHE_PREFIX_ISSUE + "*");
        Set<String> transitionKeys = redisTemplate.keys(CACHE_PREFIX_TRANSITIONS + "*");
        
        return CacheStatistics.builder()
            .issueCount(issueKeys != null ? issueKeys.size() : 0)
            .transitionsCount(transitionKeys != null ? transitionKeys.size() : 0)
            .enabled(isCacheEnabled())
            .build();
    }
    
    @Data
    @Builder
    public static class CacheStatistics {
        private int issueCount;
        private int transitionsCount;
        private boolean enabled;
    }
}

