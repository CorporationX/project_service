package faang.school.projectservice.client.jira;

import faang.school.projectservice.client.JiraRestClient;
import faang.school.projectservice.config.jira.JiraProperties;
import faang.school.projectservice.dto.jira.issue.JiraIssueFilterDto;
import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JiraRestClientImpl implements JiraRestClient {

    private final WebClient jiraWebClient;
    private final JiraProperties jiraProperties;

    public ResponseEntity<JiraCreateIssueResponse> createIssue(JiraCreateIssueRequest requestBody) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.issue())
                .toString();

        return jiraWebClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JiraCreateIssueResponse.class)
                .block();
    }

    public ResponseEntity<JiraUpdateIssueResponse> changeIssue(long issueId, JiraUpdateIssueRequest requestBody) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.issue())
                .append("/")
                .append(issueId)
                .append("?returnIssue=true")
                .toString();

        return jiraWebClient.put()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JiraUpdateIssueResponse.class)
                .block();
    }

    public ResponseEntity<JiraGetMultipleIssuesResponse> getAllIssuesWithFilter(
            String projectKey,
            JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto
    ) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.jqlSearch())
                .toString();

        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("jql", getJqlFromFilter(projectKey, jiraGetMultipleIssuesDto.getJiraIssueFilterDto()));
        queryParam.putAll(buildQueryParams(jiraGetMultipleIssuesDto));

        return jiraWebClient.get()
                .uri(uriBuilder -> {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
                    queryParam.forEach(builder::queryParam);
                    return builder.build().toUri();
                })
                .retrieve()
                .toEntity(JiraGetMultipleIssuesResponse.class)
                .block();
    }

    public ResponseEntity<JiraGetMultipleIssuesResponse> getAllIssues(
            String projectKey,
            JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto
    ) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.jqlSearch())
                .toString();

        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("jql", getJqlFromFilter(projectKey, jiraGetMultipleIssuesDto.getJiraIssueFilterDto()));
        queryParam.putAll(buildQueryParams(jiraGetMultipleIssuesDto));

        return jiraWebClient.get()
                .uri(uriBuilder -> {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
                    queryParam.forEach(builder::queryParam);
                    return builder.build().toUri();
                })
                .retrieve()
                .toEntity(JiraGetMultipleIssuesResponse.class)
                .block();
    }

    public ResponseEntity<JiraGetIssueResponse> getIssueById(long issueId) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.issue())
                .append("/")
                .append(issueId)
                .toString();

        return jiraWebClient.get()
                .uri(url)
                .retrieve()
                .toEntity(JiraGetIssueResponse.class)
                .block();
    }

    private Map<String, String> buildQueryParams(JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        Map<String, String> queryParam = new HashMap<>();

        queryParam.put("startAt", jiraGetMultipleIssuesDto.getStartAt() != null ?
                String.valueOf(jiraGetMultipleIssuesDto.getStartAt()) : jiraProperties.defaultStartAt());

        queryParam.put("maxResults", jiraGetMultipleIssuesDto.getMaxResults() == null ?
                (jiraGetMultipleIssuesDto.getLimit() != null ?
                        String.valueOf(jiraGetMultipleIssuesDto.getLimit()) : jiraProperties.defaultMaxResults())
                : String.valueOf(jiraGetMultipleIssuesDto.getMaxResults()));

        return queryParam;
    }

    private String getJqlFromFilter(String projectKey, JiraIssueFilterDto jiraIssueFilterDto) {
        StringBuilder jql = new StringBuilder("project=").append(projectKey);

        if (jiraIssueFilterDto == null) return jql.toString();

        if (jiraIssueFilterDto.getStatus() != null) {
            jql.append("+AND+status=").append(jiraIssueFilterDto.getStatus().ordinal());
        }

        if (jiraIssueFilterDto.getAssignee() != null && !jiraIssueFilterDto.getAssignee().isEmpty()) {
            jql.append("+AND+assignee=").append(jiraIssueFilterDto.getAssignee());
        }

        return jql.toString();
    }
}
