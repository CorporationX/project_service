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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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

    private static final String JIRA_REST_API_URL = "/rest/api/3";
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
    private static final Integer MAX_RESULTS_RETURNING = 500;

    private final WebClient webClient;

    @RetryJiraOperation
    public Mono<JiraTaskResponse> createJiraTask(JiraTaskCreateRequest request) {
        return createPostingRequestOnJiraClient("/issue", request, JiraTaskResponse.class);
    }

    @RetryJiraOperation
    public Mono<Void> updateJiraTask(String issueKey, JiraTaskUpdateRequest request) {
        return webClient.put()
                .uri(JIRA_REST_API_URL + "/issue/{issueKey}", issueKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(Void.class);
    }

    @RetryJiraOperation
    public Mono<Void> updateStatusJiraTask(String issueKey, JiraStatusUpdateRequest request) {
        String url = UriComponentsBuilder.fromPath("/issue/{issueKey}/transitions")
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
        return webClient.get()
                .uri(JIRA_REST_API_URL + "/issue/{issueKey}?fields", issueKey)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(JiraTaskResponse.class);
    }

    private Mono<List<JiraTaskResponse>> createGettingRequestOnJiraClient(String jql) {
        return webClient.get()
                .uri(uri -> uri.path(JIRA_REST_API_URL + "/search")
                        .queryParam("jql", jql)
                        .queryParam("fields")
                        .queryParam("maxResults", MAX_RESULTS_RETURNING)
                        .build())
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(JiraSearchResponse.class)
                .map(JiraSearchResponse::issues);
    }

    private <T> Mono<T> createPostingRequestOnJiraClient(String url, Object request, Class<T> responseType) {
        return webClient.post()
                .uri(JIRA_REST_API_URL + url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(CODE_PREDICATE_EXCEPTION, EXCEPTION_HANDLER)
                .onStatus(CODE_PREDICATE_ERROR, ERROR_HANDLER)
                .bodyToMono(responseType);
    }
}

