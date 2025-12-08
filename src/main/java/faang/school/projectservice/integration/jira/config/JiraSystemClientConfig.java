package faang.school.projectservice.integration.jira.config;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URI;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "jira.system.enabled", havingValue = "true", matchIfMissing = false)
public class JiraSystemClientConfig {

    private final JiraProperties jiraProperties;
    
    @Autowired(required = false)
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public JiraRestClient jiraRestClient() {
        JiraProperties.SystemConfig systemConfig = jiraProperties.getSystem();
        
        String activeProfile = environment != null && environment.getActiveProfiles().length > 0 
            ? String.join(",", environment.getActiveProfiles()) 
            : null;

        log.info("Initializing Jira System Client (JRJC)");
        log.debug("Active profile: {}", activeProfile);
        log.debug("Jira URL: {}", systemConfig.getBaseUrl());
        log.debug("System username: {}", systemConfig.getUsername());

        try {

            validateSystemConfig(systemConfig);

            AsynchronousJiraRestClientFactory factory =
                    new AsynchronousJiraRestClientFactory();

            JiraRestClient client = factory.createWithBasicHttpAuthentication(
                    URI.create(systemConfig.getBaseUrl()),
                    systemConfig.getUsername(),
                    systemConfig.getApiToken()
            );

            if (!isTestProfile(activeProfile)) {
                verifyConnection(client, systemConfig);
            }

            log.info("Jira System Client initialized successfully");
            log.info("Connected to: {}", systemConfig.getBaseUrl());

            return client;

        } catch (IllegalArgumentException e) {

            log.error("Invalid Jira configuration: {}", e.getMessage());

            if (isProductionProfile(activeProfile)) {
                throw new IllegalStateException(
                        "Cannot start application: Jira configuration is invalid",
                        e
                );
            } else {
                log.warn("Application started without Jira integration");
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to connect to Jira", e);

            if (isProductionProfile(activeProfile)) {
                throw new IllegalStateException(
                        "Cannot start application: Failed to connect to Jira", e);
            } else {
                log.warn("Application started without Jira connection");
                return null;
            }
        }
    }

    private void validateSystemConfig(JiraProperties.SystemConfig config) {
        if (config.getBaseUrl() == null || config.getBaseUrl().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.system.base-url is required. "
                            + "Please configure it in application.yml"
            );
        }

        if (config.getUsername() == null || config.getUsername().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.system.username is required. "
                            + "Please configure it in application.yml"
            );
        }

        if (config.getApiToken() == null || config.getApiToken().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.system.api-token is required. "
                            + "Get it from: https://id.atlassian.com/manage-profile/security/api-tokens"
            );
        }

        try {
            URI.create(config.getBaseUrl());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("jira.system.base-url has invalid format: %s", config.getBaseUrl())
            );
        }

        log.debug("System config validation passed");
    }

    private void verifyConnection(
            JiraRestClient client,
            JiraProperties.SystemConfig config
    ) {
        try {
            log.debug("Testing connection to Jira...");

            ServerInfo serverInfo = client.getMetadataClient()
                    .getServerInfo()
                    .claim();

            log.info("Connection verified");
            log.info("Server version: {}", serverInfo.getVersion());
            log.info("Build number: {}", serverInfo.getBuildNumber());
            log.info("Base URL: {}", serverInfo.getBaseUri());

        } catch (Exception e) {
            log.warn("Connection test failed (continuing anyway)", e);
            log.warn("Check credentials and network connectivity");
        }
    }

    private boolean isProductionProfile(String activeProfile) {
        return activeProfile != null && activeProfile.contains("prod");
    }

    private boolean isTestProfile(String activeProfile) {
        return activeProfile != null && activeProfile.contains("test");
    }
}
