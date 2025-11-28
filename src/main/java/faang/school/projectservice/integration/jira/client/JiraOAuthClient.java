package faang.school.projectservice.integration.jira.client;

import faang.school.projectservice.integration.jira.cache.JiraCacheService;
import faang.school.projectservice.integration.jira.config.JiraProperties;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueLinkRequest;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueRequest;
import faang.school.projectservice.integration.jira.dto.response.JiraIssueResponse;
import faang.school.projectservice.integration.jira.dto.response.JiraTransitionsResponse;
import faang.school.projectservice.integration.jira.exception.JiraApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * OAuth клиент для работы с Jira API через WebClient
 * Использует OAuth токены пользователей
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JiraOAuthClient {

    private final JiraProperties jiraProperties;
    @Qualifier("jiraOAuthWebClient")
    private final WebClient webClient;
    private final JiraCacheService cacheService;

    public String createIssue(String accessToken, JiraIssueRequest request) {
        log.debug("Creating issue via OAuth client");

        try {
            JiraIssueResponse response = webClient.post()
                    .uri("/rest/api/3/issue")
                    .header("Authorization", bearer(accessToken))
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Failed to create issue: {}", errorBody);
                                        String message = String.format("Failed to create issue: %s", errorBody);
                                        return Mono.error(new JiraApiException(message));
                                    })
                    )
                    .bodyToMono(JiraIssueResponse.class)
                    .block();

            if (response == null || response.getKey() == null) {
                throw new JiraApiException("No issue key in response");
            }

            log.info("Issue created successfully: {}", response.getKey());
            return response.getKey();

        } catch (JiraApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating issue", e);
            throw new JiraApiException("Failed to create issue", e);
        }
    }

    public void updateIssue(String accessToken, String issueKey, JiraIssueRequest request) {
        log.debug("Updating issue via OAuth client: {}", issueKey);

        try {
            webClient.put()
                    .uri("/rest/api/3/issue/{issueKey}", issueKey)
                    .header("Authorization", bearer(accessToken))
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Failed to update issue {}: {}", issueKey, errorBody);
                                        String message = String.format("Failed to update issue: %s", errorBody);
                                        return Mono.error(new JiraApiException(message));
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

            // Инвалидировать кэш
            cacheService.invalidateIssue(issueKey);

            log.info("Issue updated successfully: {}", issueKey);

        } catch (JiraApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating issue", e);
            throw new JiraApiException(String.format("Failed to update issue: %s", issueKey), e);
        }
    }


    public JiraIssueResponse getIssue(String accessToken, String issueKey) {
        log.debug("Getting issue via OAuth client: {}", issueKey);

        // Попытка получить из кэша
        Optional<JiraIssueResponse> cached = cacheService.getIssue(issueKey);
        if (cached.isPresent()) {
            log.debug("Issue retrieved from cache: {}", issueKey);
            return cached.get();
        }

        try {
            JiraIssueResponse response = webClient.get()
                    .uri("/rest/api/3/issue/{issueKey}", issueKey)
                    .header("Authorization", bearer(accessToken))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Failed to get issue {}: {}", issueKey, errorBody);
                                        String message = String.format("Failed to get issue: %s", errorBody);
                                        return Mono.error(new JiraApiException(message));
                                    })
                    )
                    .bodyToMono(JiraIssueResponse.class)
                    .block();

            if (response == null) {
                throw new JiraApiException(String.format("No response for issue: %s", issueKey));
            }

            // Сохранить в кэш
            cacheService.putIssue(issueKey, response);

            return response;

        } catch (JiraApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting issue", e);
            throw new JiraApiException(String.format("Failed to get issue: %s", issueKey), e);
        }
    }

    public JiraTransitionsResponse getTransitions(String accessToken, String issueKey) {
        log.debug("Getting transitions via OAuth client: {}", issueKey);

        // Попытка получить из кэша
        Optional<JiraTransitionsResponse> cached = cacheService.getTransitions(issueKey);
        if (cached.isPresent()) {
            log.debug("Transitions retrieved from cache: {}", issueKey);
            return cached.get();
        }

        try {
            JiraTransitionsResponse response = webClient.get()
                    .uri("/rest/api/3/issue/{issueKey}/transitions", issueKey)
                    .header("Authorization", bearer(accessToken))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Failed to get transitions for {}: {}", issueKey, errorBody);
                                        String message = String.format("Failed to get transitions: %s", errorBody);
                                        return Mono.error(new JiraApiException(message));
                                    })
                    )
                    .bodyToMono(JiraTransitionsResponse.class)
                    .block();

            if (response == null) {
                throw new JiraApiException(String.format("No transitions response for issue: %s", issueKey));
            }

            // Сохранить в кэш
            cacheService.putTransitions(issueKey, response);

            return response;

        } catch (JiraApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting transitions", e);
            throw new JiraApiException(String.format("Failed to get transitions: %s", issueKey), e);
        }
    }

    public void performTransition(String accessToken, String issueKey, String transitionId) {
        log.debug("Performing transition via OAuth client: issueKey={}, transitionId={}", issueKey, transitionId);

        try {
            webClient.post()
                    .uri("/rest/api/3/issue/{issueKey}/transitions", issueKey)
                    .header("Authorization", bearer(accessToken))
                    .bodyValue(java.util.Map.of("transition", java.util.Map.of("id", transitionId)))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Failed to perform transition for {}: {}", issueKey, errorBody);
                                        String message = String.format("Failed to perform transition: %s", errorBody);
                                        return Mono.error(new JiraApiException(message));
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

            // Инвалидировать кэш
            cacheService.invalidateIssue(issueKey);

            log.info("Transition performed successfully: {}", issueKey);

        } catch (JiraApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error performing transition", e);
            throw new JiraApiException(String.format("Failed to perform transition: %s", issueKey), e);
        }
    }

    public void linkIssues(String accessToken, JiraIssueLinkRequest request) {
        log.debug("Linking issues via OAuth client");

        try {
            webClient.post()
                    .uri("/rest/api/3/issueLink")
                    .header("Authorization", bearer(accessToken))
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Failed to link issues: {}", errorBody);
                                        String message = String.format("Failed to link issues: %s", errorBody);
                                        return Mono.error(new JiraApiException(message));
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

            // Инвалидировать кэш для обеих issues
            if (request.getInwardIssue() != null && request.getInwardIssue().getKey() != null) {
                cacheService.invalidateIssue(request.getInwardIssue().getKey());
            }
            if (request.getOutwardIssue() != null && request.getOutwardIssue().getKey() != null) {
                cacheService.invalidateIssue(request.getOutwardIssue().getKey());
            }

            log.info("Issues linked successfully");

        } catch (JiraApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error linking issues", e);
            throw new JiraApiException("Failed to link issues", e);
        }
    }

    private String bearer(String accessToken) {
        return String.format("Bearer %s", accessToken);
    }
}
