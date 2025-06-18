package faang.school.projectservice.client.jira;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JiraRestClient {

    private final RestTemplate jiraRestTemplate;
    private final JiraProperties jiraProperties;

    public ResponseEntity<JiraCreateIssueResponse> createIssue(JiraCreateIssueRequest jiraCreateIssueRequest) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.issue())
                .toString();

        return post(
                url,
                jiraCreateIssueRequest,
                JiraCreateIssueResponse.class
        );
    }

    public ResponseEntity<JiraUpdateIssueResponse> changeIssue(long issueId, JiraUpdateIssueRequest requestBody) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.issue())
                .append("/")
                .append(issueId)
                .append("?returnIssue=true")
                .toString();

        return put(
                url,
                requestBody,
                JiraUpdateIssueResponse.class
        );
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
        queryParam.putAll(getQueryParams(jiraGetMultipleIssuesDto));

        return get(
                url,
                queryParam,
                JiraGetMultipleIssuesResponse.class
        );
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
        queryParam.putAll(getQueryParams(jiraGetMultipleIssuesDto));

        return get(
                url,
                queryParam,
                JiraGetMultipleIssuesResponse.class
        );
    }

    public ResponseEntity<JiraGetIssueResponse> getIssueById(long issueId) {
        String url = new StringBuilder(jiraProperties.jiraRestApiBaseUrl())
                .append(jiraProperties.issue())
                .append("/")
                .append(issueId)
                .toString();

        return get(
                url,
                new HashMap<>(),
                JiraGetIssueResponse.class
        );
    }

    private Map<String, String> getQueryParams(JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
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
        StringBuilder jql = new StringBuilder("project=")
                .append(projectKey);

        if (jiraIssueFilterDto != null) {
            if (jiraIssueFilterDto.getStatus() != null) {
                jql.append("+AND+")
                        .append("status=")
                        .append(jiraIssueFilterDto.getStatus().ordinal());
            }

            if (jiraIssueFilterDto.getAssignee() != null && !jiraIssueFilterDto.getAssignee().isEmpty()) {
                jql.append("+AND+")
                        .append("assignee=")
                        .append(jiraIssueFilterDto.getAssignee());
            }
        }

        return jql.toString();
    }

    public <T> ResponseEntity<T> post(String url, Object body, Class<T> bodyClass) {
        return jiraRestTemplate.postForEntity(url, body, bodyClass);
    }

    public <T> ResponseEntity<T> put(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        return jiraRestTemplate.exchange(url, HttpMethod.PUT, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> get(String url, Map<String, String> queryParams, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        queryParams.forEach(builder::queryParam);
        URI uri = builder.build().toUri();
        return jiraRestTemplate.getForEntity(uri, responseType);
    }
}
