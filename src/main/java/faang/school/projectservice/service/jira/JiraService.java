package faang.school.projectservice.service.jira;

import faang.school.projectservice.config.jira.WebClientConfig;
import faang.school.projectservice.dto.jira.JiraDto;
import faang.school.projectservice.filter.jira.JiraFilter;
import faang.school.projectservice.service.jira.response.JiraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class JiraService {
    private final MyInfo myInfo;
    private final WebClientConfig webClientConfig;
    private final JiraResponse jiraResponse;
    private final List<JiraFilter> jiraFilter;

    public Mono<Object> getIssueString(String issueKey) {
        WebClient webClient = authorizeUser(1L);
        return webClient.get()
                .uri("/rest/api/3/issue/{issueKey}", issueKey)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, true))
                .onErrorResume(jiraResponse::handlerErrorConnection);
    }

    public Mono<Object> createTask(JiraDto dto) {
        WebClient webClient = authorizeUser(1L);
        return webClient.post()
                .uri("/rest/api/2/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, true))
                .onErrorResume(jiraResponse::handlerErrorConnection);

    }

    public Mono<Object> updateTask(JiraDto dto) {
        WebClient webClient = authorizeUser(1L);
        return webClient.put()
                .uri("/rest/api/2/issue/" + dto.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, false))
                .onErrorResume(jiraResponse::handlerErrorConnection);
    }

    public Mono<Object> updateTaskLink(JiraDto dto) {
        WebClient webClient = authorizeUser(1L);
        return webClient.post()
                .uri("/rest/api/3/issueLink")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, false))
                .onErrorResume(jiraResponse::handlerErrorConnection);
    }

    public Mono<String> changeTaskStatus(JiraDto dto) {
        return getAvailableTransitions(dto.getKey())
                .flatMap(transitionsResponse -> {
                    return transitionsResponse.getTransitions().stream()
                            .filter(transition -> transition.getName().equalsIgnoreCase(dto.getStatus()))
                            .findFirst()
                            .map(transition -> updateIssueStatus(dto.getKey(), transition.getId())
                                    .then(Mono.just("Status changed to: " + dto.getStatus())))
                            .orElseGet(() -> Mono.error(new RuntimeException("Transition not found for status: " + dto.getStatus())));
                })
                .onErrorResume(ex -> Mono.just("Failed to change status: " + ex.getMessage()));
    }

    private Mono<JiraDto> getAvailableTransitions(String issueKey) {
        WebClient webClient = authorizeUser(1L);
        return webClient.get()
                .uri("/rest/api/3/issue/{issueKey}/transitions", issueKey)
                .retrieve()
                .bodyToMono(JiraDto.class);
    }

    private Mono<Void> updateIssueStatus(String issueKey, String transitionId) {
        WebClient webClient = authorizeUser(1L);
        Map<String, Object> body = Map.of(
                "transition", Map.of("id", transitionId)
        );
        return webClient.post()
                .uri("/rest/api/3/issue/{issueKey}/transitions", issueKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class);

    }

    public Mono<Object> getAllTasks(String keyProject) {
        WebClient webClient = authorizeUser(1L);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/2/search")
                        .queryParam("jql", "project = " + keyProject)
                        .queryParam("maxResults", 5)
                        .build())
                .exchangeToMono(clientResponse -> jiraResponse.handler(clientResponse, true))
                .onErrorResume(jiraResponse::handlerErrorConnection);

    }

    public Mono<JiraDto> getTaskByFilters(JiraDto dto) {
        WebClient webClient = authorizeUser(1L);
        String filterStr = jiraFilter.stream()
                .filter(filter -> filter.isApplicable(dto))
                .map(filter -> filter.addFilter(dto))
                .collect(Collectors.joining(" AND "));
        String jqlQuery = String.format("project = '%s'" + filterStr, dto.getKey());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/2/search")
                        .queryParam("jql", jqlQuery)
                        .queryParam("maxResults", 5)
                        .build())
                .retrieve()
                .bodyToMono(JiraDto.class);
    }

    private WebClient authorizeUser(long userId) {
        return webClientConfig.jiraWebClient(
                myInfo.getUsername(), myInfo.getToken(), myInfo.getUrl());
    }

}
