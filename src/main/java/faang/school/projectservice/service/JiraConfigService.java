package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class JiraConfigService {

    private final ConcurrentHashMap<Long, JiraProperties> jiraConfigs = new ConcurrentHashMap<>();

    private final UserContext userContext;
    private final CryptoService cryptoService;

    public void addJiraConfig(JiraProperties jiraConfig) {
        Long userId = getUserId();
        jiraConfig.setApiToken(cryptoService.encrypt(jiraConfig.getApiToken()));
        jiraConfigs.put(userId, jiraConfig);
        log.debug("Added new Jira config on address: {}", jiraConfig.getBaseUrl());
    }

    public JiraProperties getJiraConfig() {
        Long userId = getUserId();
        if (!jiraConfigs.containsKey(userId)) {
            throw new EntityNotFoundException("Jira config user with id {} not found", userId);
        }
        JiraProperties jiraConfig = jiraConfigs.get(userId);
        jiraConfig.setApiToken(cryptoService.decrypt(jiraConfig.getApiToken()));
        return jiraConfig;
    }

    private Long getUserId() {
        return userContext.getUserId();
    }
}
