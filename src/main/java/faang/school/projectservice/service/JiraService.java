package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueDto;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;

public interface JiraService {

    JiraCreateIssueResponseDto createIssue(JiraCreateIssueDto requestBody);

    JiraUpdateIssueResponse changeIssue(long issueId, JiraUpdateIssueRequest requestBody);

    JiraGetMultipleIssuesResponse getAllIssuesWithFilter(String projectKey,
                                                         JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto);

    JiraGetMultipleIssuesResponse getAllIssues(String projectKey,
                                               JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto);

    JiraGetIssueResponseDto getIssueById(long issueId);
}