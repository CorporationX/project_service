package faang.school.projectservice.integration.jira.config;

import faang.school.projectservice.integration.jira.metrics.JiraMetricsService;
import faang.school.projectservice.integration.jira.oauth.model.JiraOAuthTokenRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Конфигурация для регистрации Gauge метрик
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JiraMetricsConfig {
    
    private final JiraMetricsService metricsService;
    private final JiraOAuthTokenRepository tokenRepository;
    private final TaskRepository taskRepository;
    
    @Bean
    public ApplicationRunner registerGaugeMetrics() {
        return args -> {
            // Регистрация Gauge для активных OAuth токенов
            metricsService.registerActiveTokensGauge(() -> 
                tokenRepository.countActiveTokens(LocalDateTime.now())
            );
            
            // Регистрация Gauge для несинхронизированных задач
            metricsService.registerUnsyncedTasksGauge(() -> 
                taskRepository.findAll().stream()
                    .filter(t -> t.getJiraIssueKey() == null)
                    .count()
            );
            
            log.info("Gauge metrics registered successfully");
        };
    }
}

