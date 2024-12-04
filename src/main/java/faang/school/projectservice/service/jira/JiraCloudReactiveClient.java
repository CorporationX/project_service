package faang.school.projectservice.service.jira;

import faang.school.projectservice.config.jira.JiraUriConfig;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchResponse;
import faang.school.projectservice.dto.jira.issue.IssueResponse;
import faang.school.projectservice.dto.jira.issue.create.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.enums.IssueStatus;
import faang.school.projectservice.dto.jira.issue.update.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchRequest;
import faang.school.projectservice.dto.jira.issue.transition.TransitionNestedResponse;
import faang.school.projectservice.dto.jira.issue.transition.TransitionRequest;
import faang.school.projectservice.dto.jira.issue.transition.TransitionsResponse;
import faang.school.projectservice.exceptions.jira.JiraBadRequestException;
import faang.school.projectservice.exceptions.jira.JiraNotFoundException;
import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.impl.IssueKeyMatchCondition;
import faang.school.projectservice.service.jira.search.conditions.impl.ProjectCondition;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class JiraCloudReactiveClient {

    private static final String CIRCUIT_BREAKER_NAME = "jira-api-circuit-breaker";
    private static final String RETRY_CONFIG_NAME = "jira-api-retry";

    private final WebClient webClient;
    private final JiraUriConfig uriConfig;

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<IssueResponse> createIssue(JiraCreateIssueRequest request) {
        log.info("Starting to Create issue {}", request);
        return webClient.post()
                .uri(uriConfig.getCreateIssueUri())
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraBadRequestException(body)))
                )
                .onStatus(status -> status == NOT_FOUND, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraNotFoundException(body)))
                )
                .bodyToMono(IssueResponse.class);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<IssueResponse> updateIssue(String issueKey, JiraUpdateIssueRequest updateRequest) {
        log.info("Starting to Update issue {}", issueKey);

        String updateUrl = String.format(uriConfig.getUpdateIssueUri(), issueKey);
        return webClient.put()
                .uri(updateUrl)
                .bodyValue(updateRequest)
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraBadRequestException(body)))
                )
                .onStatus(status -> status == NOT_FOUND, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraNotFoundException(body)))
                )
                .bodyToMono(IssueResponse.class);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<Void> transitionIssue(String issueKey, IssueStatus newIssueStatus) {
        log.info("Starting to transition issue with key {} to {}", issueKey, newIssueStatus);

        String url = String.format(uriConfig.getTransitionIssueUri(), issueKey);
        TransitionRequest request = TransitionRequest.builder()
                .transition(new TransitionRequest.TransitionNested(newIssueStatus.getId()))
                .build();

        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraBadRequestException(body)))
                )
                .onStatus(status -> status == NOT_FOUND, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraNotFoundException(body)))
                )
                .bodyToMono(Void.class);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<List<TransitionNestedResponse>> getAvailableTransitions(String issueKey) {
        log.info("Starting to retrieve available transitions for issue {}", issueKey);

        String url = String.format(uriConfig.getTransitionIssueUri(), issueKey);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraBadRequestException(body)))
                )
                .onStatus(status -> status == NOT_FOUND, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraNotFoundException(body)))
                )
                .bodyToMono(TransitionsResponse.class)
                .map(TransitionsResponse::transitions);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<List<IssueResponse>> searchIssuesByFilter(JiraSearchRequest request) {
        log.info("Starting to search issues by filter {}", request);

        return webClient
                .post()
                .uri(uriConfig.getSearchUri())
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status == BAD_REQUEST, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraBadRequestException(body)))
                )
                .onStatus(status -> status == NOT_FOUND, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new JiraNotFoundException(body)))
                )
                .bodyToMono(JiraSearchResponse.class)
                .map(JiraSearchResponse::getIssues);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<IssueResponse> getIssueByKey(String issueKey) {
        log.info("Starting to retrieve issue by key {}", issueKey);

        JiraSearchRequest searchRequest = new JiraSearchRequestBuilder()
                .withDefaultFields()
                .addCondition(new IssueKeyMatchCondition(issueKey))
                .maxResults(1)
                .build();
        Mono<List<IssueResponse>> jiraIssues = searchIssuesByFilter(searchRequest);

        return jiraIssues
                .flatMap(issues -> Mono.justOrEmpty(issues.stream().findFirst()));
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = RETRY_CONFIG_NAME)
    public Mono<List<IssueResponse>> getIssuesByProject(String projectKey) {
        log.info("Starting to retrieve issues by project key {}", projectKey);

        JiraSearchRequest searchRequest = new JiraSearchRequestBuilder()
                .withDefaultFields()
                .addCondition(new ProjectCondition(projectKey))
                .build();

        return searchIssuesByFilter(searchRequest);
    }
}
