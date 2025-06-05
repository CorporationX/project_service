package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.task.JiraIssueFilterDto;

import java.util.Map;

public interface JiraService {

    Map<String, Object> createIssue(Map json);

    Map<String, Object> changeIssue(long issueId, Map json);

    Map<String, Object> getAllIssuesWithFilter(String projectKey, JiraIssueFilterDto jiraIssueFilterDto, int startAt, int maxResults, Integer limit);

    Map<String, Object> getAllIssues(String projectKey, int startAt, int maxResults, Integer limit);

    Map<String, Object> getIssueById(long issueId);
}