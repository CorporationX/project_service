package faang.school.projectservice.gateway;

import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;


public interface JiraClientGateway {

    JiraIssueResponse createIssue(JiraIssueRequest request);

    void updateIssue(String issueIdOrKey, JiraIssueRequest request);

    JiraIssueResponse getIssue(String issueIdOrKey);

    JiraSearchResponse searchIssues(String jqlQuery, Integer startAt, Integer maxResults, String fields);
}