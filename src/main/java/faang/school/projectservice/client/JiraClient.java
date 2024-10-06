package faang.school.projectservice.client;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;
import faang.school.projectservice.dto.jira.JiraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JiraClient {

    private final WebClient webClient;

    public IssueDto createIssue(IssueDto issueDto) {
        return webClient.post()
                .uri("/issue")
                .bodyValue(issueDto)
                .retrieve()
                .bodyToMono(IssueDto.class)
                .block();
    }

    public IssueDto getIssueByKey(String issueKey) {
        return webClient.get()
                .uri("/issue/{key}", issueKey)
                .retrieve()
                .bodyToMono(IssueDto.class)
                .block();
    }

    public void createIssueLinks(List<IssueUpdateDto.IssueLink> issueUpdateDtos) {
        issueUpdateDtos.forEach(issueLink -> webClient.post()
                .uri("/issueLink")
                .bodyValue(issueLink)
                .retrieve()
                .toBodilessEntity()
                .block()
        );
    }

    public void setTransitionByKey(String issueKey, IssueUpdateDto.Transition transitionDto) {
        webClient.post()
                .uri("/issue/{issueKey}/transitions", issueKey)
                .bodyValue(Map.of("transition", transitionDto))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void updateIssueByKey(String issueKey, IssueUpdateDto issueDto) {
        webClient.put()
                .uri("/issue/{key}", issueKey)
                .bodyValue(issueDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public JiraResponse getProjectInfoByJql(String jql) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("jql", jql)
                        .build())
                .retrieve()
                .bodyToMono(JiraResponse.class)
                .block();
    }
}
