package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;

public interface JiraService {

    JiraCreateIssueResponse createIssue(JiraCreateIssueRequest requestBody);

    JiraUpdateIssueResponse changeIssue(long issueId, JiraUpdateIssueRequest requestBody);

    JiraGetMultipleIssuesResponse getAllIssuesWithFilter(String projectKey,
                                                         JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto);

    JiraGetMultipleIssuesResponse getAllIssues(String projectKey,
                                               JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto);

    JiraGetIssueResponse getIssueById(long issueId);
}