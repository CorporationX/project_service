package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.jira.WebClientConfig;
import faang.school.projectservice.model.dto.jira.JiraDto;
import faang.school.projectservice.filter.jira.JiraFilter;
import faang.school.projectservice.model.dto.client.UserDto;
import faang.school.projectservice.service.jira.response.JiraResponse;
import faang.school.projectservice.util.JiraPath;
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
public class JiraServiceImpl implements JiraService {
    private final WebClientConfig webClientConfig;
    private final JiraResponse jiraResponse;
    private final List<JiraFilter> jiraFilter;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Override
    public Mono<Object> getIssue(String issueKey) {
        WebClient webClient = authorizeUser();
        return webClient.get()
                .uri(JiraPath.GET_TASK, issueKey)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, true));
    }

    @Override
    public Mono<Object> createTask(JiraDto dto) {
        WebClient webClient = authorizeUser();
        return webClient.post()
                .uri(JiraPath.CREATE_TASK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, true));
    }

    @Override
    public Mono<Object> updateTask(JiraDto dto) {
        WebClient webClient = authorizeUser();
        return webClient.put()
                .uri(JiraPath.UPDATE_TASK, dto.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, false));

    }

    @Override
    public Mono<Object> updateTaskLink(JiraDto dto) {
        WebClient webClient = authorizeUser();
        return webClient.post()
                .uri(JiraPath.UPDATE_LINK_TASK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse ->
                        jiraResponse.handler(clientResponse, false));

    }

    @Override
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
        WebClient webClient = authorizeUser();
        return webClient.get()
                .uri(JiraPath.GET_TRANSITIONS, issueKey)
                .retrieve()
                .bodyToMono(JiraDto.class);
    }

    private Mono<Void> updateIssueStatus(String issueKey, String transitionId) {
        WebClient webClient = authorizeUser();
        Map<String, Object> body = Map.of(
                "transition", Map.of("id", transitionId)
        );
        return webClient.post()
                .uri(JiraPath.GET_TRANSITIONS, issueKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Object> getAllTasks(String keyProject) {
        WebClient webClient = authorizeUser();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(JiraPath.SEARCH)
                        .queryParam("jql", "project = " + keyProject)
                        .queryParam("maxResults", 5)
                        .build())
                .exchangeToMono(clientResponse -> jiraResponse.handler(clientResponse, true));
    }

    @Override
    public Mono<JiraDto> getTaskByFilters(JiraDto dto) {
        WebClient webClient = authorizeUser();

        String filterStr = jiraFilter.stream()
                .filter(filter -> filter.isApplicable(dto))
                .map(filter -> filter.addFilter(dto))
                .collect(Collectors.joining(" AND "));
        String jqlQuery = String.format("project = '%s'" + filterStr, dto.getKey());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(JiraPath.SEARCH)
                        .queryParam("jql", jqlQuery)
                        .queryParam("maxResults", 5)
                        .build())
                .retrieve()
                .bodyToMono(JiraDto.class);
    }

    private WebClient authorizeUser() {
        long userId = userContext.getUserId();
        UserDto userDto = userServiceClient.getUser(userId);
        return webClientConfig.jiraWebClient(
                userDto.getEmail(), userDto.getToken(), userDto.getProjectUrl());
    }
}
