package faang.school.projectservice.client;

import faang.school.projectservice.annotation.RetryJiraOperation;
import faang.school.projectservice.dto.jiratask.JiraSearchResponse;
import faang.school.projectservice.dto.jiratask.JiraStatusUpdateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskCreateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskResponse;
import faang.school.projectservice.dto.jiratask.JiraTaskUpdateRequest;
import faang.school.projectservice.exception.JiraApiException;
import faang.school.projectservice.exception.JiraConnectionException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Component
public class JiraClient {

    private static final Predicate<HttpStatusCode> CODE_PREDICATE_EXCEPTION =
            HttpStatusCode::is4xxClientError;
    private static final Predicate<HttpStatusCode> CODE_PREDICATE_ERROR =
            HttpStatusCode::is5xxServerError;
    private static final Function<ClientResponse, Mono<? extends Throwable>> EXCEPTION_HANDLER =
            response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new JiraApiException(error)));
    private static final Function<ClientResponse, Mono<? extends Throwable>> ERROR_HANDLER =
            response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new JiraConnectionException(error)));

    private final WebClient webClient;

    @Value("${jira-api-setting.jira-api-url}")
    private String jiraRestApiUrl;

    @Value("${jira-api-setting.max-results-returning}")
    private Integer maxResultsReturning;

    @RetryJiraOperation
    public Mono<JiraTaskResponse> createJiraTask(JiraTaskCreateRequest request) {
        String url = UriComponentsBuilder.fromPath(jiraRestApiUrl + "/issue")
                .toUriString();

        return createPostingRequestOnJiraClient(url, request, JiraTaskResponse.class);
    }

    @RetryJiraOperation
    public Mono<ResponseEntity<Void>> updateJiraTask(String issueKey, JiraTaskUpdateRequest request) {
        String url = UriComponentsBuilder.fromPath(jiraRestApiUrl + "/issue/{issueKey}")
                .buildAndExpand(issueKey)
                .toUriString();

        return webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .toBodilessEntity();
    }

    @RetryJiraOperation
    public Mono<Void> updateStatusJiraTask(String issueKey, JiraStatusUpdateRequest request) {
        String url = UriComponentsBuilder.fromPath(jiraRestApiUrl + "/issue/{issueKey}/transitions")
                .buildAndExpand(issueKey)
                .toUriString();
        return createPostingRequestOnJiraClient(url, request, Void.class);
    }

    @RetryJiraOperation
    public Mono<List<JiraTaskResponse>> getProjectJiraTasksByFilters(@NotBlank String projectKey,
                                                                     @NotBlank String status,
                                                                     @NotBlank String assignee) {
        String jql = String.format("project = \"%s\" AND status = \"%s\" AND assignee = \"%s\"",
                projectKey, status, assignee);
        return createGettingRequestOnJiraClient(jql);
    }

    @RetryJiraOperation
    public Mono<List<JiraTaskResponse>> getProjectJiraTasks(@NotBlank String projectKey) {
        String jql = String.format("project = \"%s\"", projectKey);
        return createGettingRequestOnJiraClient(jql);
    }

    @RetryJiraOperation
    public Mono<JiraTaskResponse> getJiraTaskById(String issueKey) {
        String url = UriComponentsBuilder.fromPath(jiraRestApiUrl + "/issue/{issueKey}?fields")
                .buildAndExpand(issueKey)
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(JiraTaskResponse.class);
    }

    private Mono<List<JiraTaskResponse>> createGettingRequestOnJiraClient(String jql) {
        String url = UriComponentsBuilder.fromPath(jiraRestApiUrl + "/search")
                .queryParam("jql", jql)
                .queryParam("maxResults", maxResultsReturning)
                .build().toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(JiraSearchResponse.class)
                .map(JiraSearchResponse::issues);
    }

    private <T> Mono<T> createPostingRequestOnJiraClient(String url, Object request, Class<T> responseType) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(responseType);
    }
}

