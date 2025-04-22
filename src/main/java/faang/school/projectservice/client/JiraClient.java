package faang.school.projectservice.client;

import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.update.IssueLinkDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.dto.jira.update.TransitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JiraClient {
    private final WebClient jiraWebClient;

    public Mono<IssueCreateResponseDto> createIssue(IssueRequestDto issueRequestDto) {
        return jiraWebClient.post()
                .uri("/issue")
                .bodyValue(issueRequestDto)
                .retrieve()
                .bodyToMono(IssueCreateResponseDto.class);
    }

    public Mono<IssueResponseDto> getIssueByKey(String issueKey) {
        return jiraWebClient.get()
                .uri("/issue/{key}", issueKey)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new IllegalArgumentException(
                                "Issue with key " + issueKey + " not found")))
                .bodyToMono(IssueResponseDto.class);
    }

    public Mono<Void> createIssueLinks(List<IssueLinkDto> issueLinkDtos) {
        return Flux.fromIterable(issueLinkDtos)
                .flatMap(issueLink -> jiraWebClient.post()
                        .uri("/issueLink")
                        .bodyValue(issueLink)
                        .retrieve()
                        .toBodilessEntity()
                )
                .then();
    }

    public Mono<Void> setTransitionByKey(String issueKey, TransitionDto transition) {
        return jiraWebClient.post()
                .uri("/issue/{issueKey}/transitions", issueKey)
                .bodyValue(Map.of("transition", transition))
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    public Mono<Void> updateIssueByKey(String issueKey, IssueUpdateDto issueUpdateDto) {
        return jiraWebClient.put()
                .uri("/issue/{key}", issueKey)
                .bodyValue(issueUpdateDto)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    public Mono<IssuesResponseDto> getInfoByJql(String query) {
        return jiraWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("jql", query)
                        .build())
                .retrieve()
                .bodyToMono(IssuesResponseDto.class);
    }
}