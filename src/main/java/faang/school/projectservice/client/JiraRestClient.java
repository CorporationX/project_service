package faang.school.projectservice.client;

import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;
import org.springframework.http.ResponseEntity;

public interface JiraRestClient {

    ResponseEntity<JiraCreateIssueResponse> createIssue(JiraCreateIssueRequest jiraCreateIssueRequest);

    ResponseEntity<JiraUpdateIssueResponse> changeIssue(long issueId, JiraUpdateIssueRequest requestBody);

    ResponseEntity<JiraGetMultipleIssuesResponse> getAllIssuesWithFilter(
            String projectKey,
            JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto
    );

    ResponseEntity<JiraGetMultipleIssuesResponse> getAllIssues(
            String projectKey,
            JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto
    );

    ResponseEntity<JiraGetIssueResponse> getIssueById(long issueId);
}
