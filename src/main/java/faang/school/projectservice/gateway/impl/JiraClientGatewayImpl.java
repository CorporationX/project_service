package faang.school.projectservice.gateway.impl;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;
import faang.school.projectservice.gateway.JiraClientGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JiraClientGatewayImpl implements JiraClientGateway {

    private final JiraClient jiraClient;

    @Override
    public JiraIssueResponse createIssue(JiraIssueRequest request) {
        return jiraClient.createIssue(request).getBody();
    }

    @Override
    public void updateIssue(String issueIdOrKey, JiraIssueRequest request) {
        jiraClient.updateIssue(issueIdOrKey, request);
    }

    @Override
    public JiraIssueResponse getIssue(String issueIdOrKey) {
        return jiraClient.getIssue(issueIdOrKey).getBody();
    }

    @Override
    public JiraSearchResponse searchIssues(String jqlQuery, Integer startAt, Integer maxResults, String fields) {
        return jiraClient.searchIssues(jqlQuery, startAt, maxResults, fields).getBody();
    }
}