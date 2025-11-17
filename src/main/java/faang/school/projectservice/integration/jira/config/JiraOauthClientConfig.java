package faang.school.projectservice.integration.jira.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "jira.Oauth", name = "enabled", havingValue = "true")
public class JiraOAuthClientConfig {

    private final JiraProperties jiraProperties;

    @Bean
    public WebClient jiraOauthWebClient() {
        JiraProperties.SystemConfig systemConfig = jiraProperties.getSystem();
        JiraProperties.OauthConfig oauthConfig = jiraProperties.getOauth();

        log.info("Initializing Jira Oauth WebClient");
        log.debug("Oauth enabled: {}", oauthConfig.isEnable());

        try {

            validateOauthConfig(oauthConfig);

            validateBaseUrl(systemConfig);

            log.info("Jira Oauth WebClient initialized successfully");
            String baseUrl = systemConfig.getBaseUrl() + "/rest/api/3";
            log.info("Base URL: {}", baseUrl);
            log.info("Redirect URI: {}", oauthConfig.getRedirectUri());

            return WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("Accept", "application/json")
                    .filter(logRequest())
                    .filter(logResponse())
                    .filter(handleErrors())
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid Oauth configuration: {}", e.getMessage());
            throw new IllegalStateException(
                    "Cannot initialize Jira Oauth client: invalid configuration", e);

        } catch (Exception e) {
            log.error("Failed to initialize Jira Oauth WebClient", e);
            throw new IllegalStateException(
                    "Cannot initialize Jira Oauth client",
                    e
            );
        }
    }

    private void validateOauthConfig(JiraProperties.OauthConfig config) {
        if (!config.isEnable()) {
            throw new IllegalArgumentException(
                    "Oauth is not enabled. Set jira.Oauth.enabled=true"
            );
        }

        if (config.getClientId() == null || config.getClientId().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.Oauth.client-id is required when Oauth is enabled. "
                            + "Get it from Atlassian Developer Console: "
                            + "https://developer.atlassian.com/console/myapps/"
            );
        }

        if (config.getClientSecret() == null || config.getClientSecret().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.Oauth.client-secret is required when Oauth is enabled"
            );
        }

        if (config.getRedirectUri() == null || config.getRedirectUri().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.Oauth.redirect-uri is required. "
                            + "Example: http://localhost:8080/api/jira/Oauth/callback"
            );
        }

        try {
            URI.create(config.getRedirectUri());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "jira.Oauth.redirect-uri has invalid format: "
                            + config.getRedirectUri()
            );
        }

        if (config.getAuthorizationUri() == null || config.getAuthorizationUri().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.Oauth.authorization-uri is required"
            );
        }

        if (config.getTokenUri() == null || config.getTokenUri().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.Oauth.token-uri is required"
            );
        }

        log.debug("Oauth config validation passed");
    }

    private void validateBaseUrl(JiraProperties.SystemConfig config) {
        if (config.getBaseUrl() == null || config.getBaseUrl().isBlank()) {
            throw new IllegalArgumentException(
                    "jira.system.base-url is required for Oauth client"
            );
        }

        try {
            URI.create(config.getBaseUrl());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "jira.system.base-url has invalid format: " + config.getBaseUrl()
            );
        }

        log.debug("Base URL validation passed");
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                log.debug("Oauth Request: {} {}", clientRequest.url());
                clientRequest.headers().forEach((name, values) -> {
                    if (!"Authorization".equalsIgnoreCase(name)) {
                        log.debug("  {}: {}", name, values);
                    }
                });
            }
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                log.debug("Oauth Response: Status {}", clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction handleErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.warn("Oauth API returned error: {} {}",
                        clientResponse.statusCode().value(),
                        getStatusDescription(clientResponse.statusCode())
                );
            }
            return Mono.just(clientResponse);
        });
    }

    private String getStatusDescription(HttpStatusCode statusCode) {
        HttpStatus resolved = HttpStatus.resolve(statusCode.value());
        return resolved != null ? resolved.getReasonPhrase() : "Unknown Status";
    }
}
