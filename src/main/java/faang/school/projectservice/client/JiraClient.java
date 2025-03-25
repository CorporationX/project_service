package faang.school.projectservice.client;

import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.update.IssueLinkDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JiraClient {
    private final WebClient jiraWebClient;

    public IssueCreateResponseDto createIssue(IssueRequestDto issueRequestDto) {
        return jiraWebClient.post()
                .uri("/issue")
                .bodyValue(issueRequestDto)
                .retrieve()
                .bodyToMono(IssueCreateResponseDto.class)
                .block();
    }

    public IssueResponseDto getIssueByKey(String issueKey) {
        return jiraWebClient.get()
                .uri("/issue/{key}", issueKey)
                .retrieve()
                .bodyToMono(IssueResponseDto.class)
                .block();
    }

    public void createIssueLinks(List<IssueLinkDto> issueLinkDtos) {
        issueLinkDtos.forEach(issueLink -> jiraWebClient.post()
                .uri("/issueLink")
                .bodyValue(issueLink)
                .retrieve()
                .toBodilessEntity()
                .block()
        );
    }

    public void setTransitionByKey(String issueKey, String transitionKey) {
        jiraWebClient.post()
                .uri("/issue/{issueKey}/transitions", issueKey)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void updateIssueByKey(String issueKey, IssueUpdateDto issueUpdateDto) {
        jiraWebClient.put()
                .uri("/issue/{key}", issueKey)
                .bodyValue(issueUpdateDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public IssuesResponseDto getInfoByJql(String jql) {
        return jiraWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("jql", jql)
                        .build())
                .retrieve()
                .bodyToMono(IssuesResponseDto.class)
                .block();
    }
}