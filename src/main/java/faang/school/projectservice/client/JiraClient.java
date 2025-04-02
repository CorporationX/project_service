package faang.school.projectservice.client;

import faang.school.projectservice.dto.jiratask.JiraTaskCreateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskResponse;
import faang.school.projectservice.dto.jiratask.JiraTaskUpdateRequest;
import faang.school.projectservice.exception.JiraApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Component
@Slf4j
public class JiraClient {

    private static final String JIRA_REST_API_URL = "/rest/api/3";
    private static final Predicate<HttpStatusCode> CODE_PREDICATE =
            code -> code.is4xxClientError() || code.is5xxServerError();
    private static final Function<ClientResponse, Mono<? extends Throwable>> ERROR_HANDLER =
            response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new JiraApiException(error)));

    private final WebClient webClient;

    public Mono<JiraTaskResponse> createJiraTask(JiraTaskCreateRequest request) {
        return webClient.post()
                .uri(JIRA_REST_API_URL + "/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(CODE_PREDICATE, ERROR_HANDLER)
                .bodyToMono(JiraTaskResponse.class)
                .doOnSuccess(response -> log.debug("Task created with key: {}", response.key()))
                .doOnError(e -> log.error("Failed to create task", e));
    }

    public Mono<Void> updateJiraTask(String issueKey, JiraTaskUpdateRequest request) {
        return webClient.put()
                .uri(JIRA_REST_API_URL + "/issue/{issueKey}", issueKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(CODE_PREDICATE, ERROR_HANDLER)
                .bodyToMono(Void.class)
                .doOnSuccess(response -> log.debug("Task updated with key: {}", issueKey))
                .doOnError(e -> log.error("Failed to update task", e));
    }

    public Mono<List<JiraTaskResponse>> getProjectJiraTasksByFilters() {
        return null;
    }

    public Mono<List<JiraTaskResponse>> getProjectJiraTasks() {
        return null;
    }

    public Mono<JiraTaskResponse> getJiraTaskById(String issueKey) {
        return null;
    }
}

