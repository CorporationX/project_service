package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class JiraConfigService {

    private static final int VALUE_OLD_CONFIGS = 20;

    private final ConcurrentHashMap<Long, JiraProperties> jiraConfigs = new ConcurrentHashMap<>();
    private final UserContext userContext;
    private final CryptoService cryptoService;

    public void addJiraConfig(JiraProperties jiraConfig) {
        Long userId = getUserId();
        jiraConfig.setApiToken(cryptoService.encrypt(jiraConfig.getApiToken()));
        jiraConfig.setUsedAt(Instant.now());
        jiraConfigs.put(userId, jiraConfig);
        log.debug("Added new Jira config on address: {}", jiraConfig.getBaseUrl());
    }

    public JiraProperties getJiraConfig() {
        Long userId = getUserId();
        if (!jiraConfigs.containsKey(userId)) {
            throw new EntityNotFoundException("Jira config user with id %s not found", userId.toString());
        }
        JiraProperties jiraConfig = jiraConfigs.get(userId);
        jiraConfig.setUsedAt(Instant.now());
        return JiraProperties.builder()
                .baseUrl(jiraConfig.getBaseUrl())
                .apiToken(cryptoService.decrypt(jiraConfig.getApiToken()))
                .email(jiraConfig.getEmail())
                .usedAt(jiraConfig.getUsedAt())
                .build();
    }

    @Async("tokenClearer")
    @Scheduled(cron = "${app.scheduler.jira-token-cleanup}")
    public void clearOldJiraConfigs() {
        log.debug("Start clearing old Jira configs");
        Instant currentTime = Instant.now();
        jiraConfigs.entrySet().removeIf(entry -> {
            Instant usedAt = entry.getValue().getUsedAt();
            return Duration.between(usedAt, currentTime).toMinutes() >= VALUE_OLD_CONFIGS;
        });
        log.debug("Old Jira configs have been cleared");
    }

    private Long getUserId() {
        return userContext.getUserId();
    }
}
