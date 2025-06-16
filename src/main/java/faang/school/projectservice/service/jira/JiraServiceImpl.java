package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.jira.JiraRestClient;
import faang.school.projectservice.config.jira.JiraProperties;
import faang.school.projectservice.dto.jira.issue.JiraIssueFilterDto;
import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueDto;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraRestClient jiraRestClient;
    private final JiraProperties jiraProperties;


    @Override
    public JiraCreateIssueResponseDto createIssue(JiraCreateIssueDto jiraCreateIssueDto) {
        String url = new StringBuilder()
                .append(jiraProperties.baseUrl())
                .append(jiraProperties.restApiUrl())
                .append(jiraProperties.issue())
                .toString();

        try {
            ResponseEntity<JiraCreateIssueResponseDto> response = jiraRestClient.post(
                    url,
                    jiraCreateIssueDto,
                    JiraCreateIssueResponseDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public JiraUpdateIssueResponse changeIssue(long issueId, JiraUpdateIssueRequest requestBody) {
        String url = new StringBuilder()
                .append(jiraProperties.baseUrl())
                .append(jiraProperties.restApiUrl())
                .append(jiraProperties.issue())
                .append("/")
                .append(issueId)
                .append("?returnIssue=true")
                .toString();

        try {
            ResponseEntity<JiraUpdateIssueResponse> response = jiraRestClient.put(
                    url,
                    requestBody,
                    JiraUpdateIssueResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                log.info("Task {} updated successfully and full issue data returned. Status: {}",
                        issueId,
                        response.getStatusCode());

                return response.getBody();
            } else {
                log.warn("Task {} updated, but no body or unexpected status: {}", issueId, response.getStatusCode());
                return new JiraUpdateIssueResponse();
            }
        } catch (HttpClientErrorException e) {
            log.error("Error updating task {}. Response Body: {}", issueId, e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    @Override
    public JiraGetMultipleIssuesResponse getAllIssuesWithFilter(String projectKey,
                                                                JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        try {
            String url = new StringBuilder()
                    .append(jiraProperties.baseUrl())
                    .append(jiraProperties.restApiUrl())
                    .append(jiraProperties.jqlSearch())
                    .toString();

            Map<String, String> queryParam = getQueryParams(jiraGetMultipleIssuesDto);
            queryParam.put("jql", getJqlFromFilter(projectKey, jiraGetMultipleIssuesDto.getJiraTaskFilterDto()));

            ResponseEntity<JiraGetMultipleIssuesResponse> response = jiraRestClient.get(
                    url,
                    queryParam,
                    JiraGetMultipleIssuesResponse.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public JiraGetMultipleIssuesResponse getAllIssues(String projectKey,
                                                      JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        try {
            String url = new StringBuilder()
                    .append(jiraProperties.baseUrl())
                    .append(jiraProperties.restApiUrl())
                    .append(jiraProperties.jqlSearch())
                    .toString();

            Map<String, String> queryParam = getQueryParams(jiraGetMultipleIssuesDto);

            ResponseEntity<JiraGetMultipleIssuesResponse> response = jiraRestClient.get(
                    url,
                    queryParam,
                    JiraGetMultipleIssuesResponse.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public JiraGetIssueResponseDto getIssueById(long issueId) {
        String url = new StringBuilder()
                .append(jiraProperties.baseUrl())
                .append(jiraProperties.restApiUrl())
                .append(jiraProperties.issue())
                .append("/")
                .append(issueId)
                .toString();
        try {
            ResponseEntity<JiraGetIssueResponseDto> response = jiraRestClient.get(
                    url,
                    new HashMap<>(),
                    JiraGetIssueResponseDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    private Map<String, String> getQueryParams(JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        Map<String, String> queryParam = new HashMap<>();

        queryParam.put("startAt", jiraGetMultipleIssuesDto.getStartAt() != null ?
                String.valueOf(jiraGetMultipleIssuesDto.getStartAt()) : "0");

        queryParam.put("maxResults", jiraGetMultipleIssuesDto.getMaxResults() == null ?
                (jiraGetMultipleIssuesDto.getLimit() != null ?
                        String.valueOf(jiraGetMultipleIssuesDto.getLimit()) : "50")
                : String.valueOf(jiraGetMultipleIssuesDto.getMaxResults()));

        return queryParam;
    }

    private String getJqlFromFilter(String projectKey, JiraIssueFilterDto jiraIssueFilterDto) {
        StringBuilder jql = new StringBuilder("project=")
                .append(projectKey);

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

        return jql.toString();
    }
}