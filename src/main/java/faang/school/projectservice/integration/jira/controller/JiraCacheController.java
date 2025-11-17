package faang.school.projectservice.integration.jira.controller;

import faang.school.projectservice.integration.jira.cache.JiraCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления кэшем Jira
 */
@Slf4j
@RestController
@RequestMapping("/api/jira/cache")
@RequiredArgsConstructor
public class JiraCacheController {
    
    private final JiraCacheService cacheService;
    
    /**
     * Очистить весь кэш Jira
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCache() {
        log.info("Clearing Jira cache");
        cacheService.clearCache();
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Инвалидировать кэш для конкретной Issue
     */
    @DeleteMapping("/issue/{issueKey}")
    public ResponseEntity<Void> invalidateIssue(@PathVariable String issueKey) {
        log.info("Invalidating cache for issue: {}", issueKey);
        cacheService.invalidateIssue(issueKey);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Получить статистику кэша
     */
    @GetMapping("/statistics")
    public ResponseEntity<JiraCacheService.CacheStatistics> getStatistics() {
        log.debug("Getting cache statistics");
        JiraCacheService.CacheStatistics statistics = cacheService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}

