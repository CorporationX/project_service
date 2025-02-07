package faang.school.projectservice.gateway;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JiraClientGateway {

    private final JiraClient jiraClient;

    public JiraIssueResponse createIssue(JiraIssueRequest request) {
        return jiraClient.createIssue(request).getBody();
    }

    public void updateIssue(String issueIdOrKey, JiraIssueRequest request) {
        jiraClient.updateIssue(issueIdOrKey, request);
    }

    public JiraIssueResponse getIssue(String issueIdOrKey) {
        return jiraClient.getIssue(issueIdOrKey).getBody();
    }

    public JiraSearchResponse searchIssues(String jqlQuery, Integer startAt, Integer maxResults, String fields) {
        return jiraClient.searchIssues(jqlQuery, startAt, maxResults, fields).getBody();
    }
}