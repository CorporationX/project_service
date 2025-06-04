package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.task.JiraCreateIssueJSON;
import faang.school.projectservice.dto.jira.task.JiraCreateTaskDto;
import faang.school.projectservice.dto.jira.task.JiraChangeTaskDto;
import faang.school.projectservice.dto.jira.task.JiraTaskFilterDto;

import java.util.Map;

public interface JiraService {

    Map<String, Object> createIssue(JiraCreateTaskDto jiraCreateTaskDto);
    Map<String, Object> changeIssue(long issueId, JiraChangeTaskDto jiraTaskDto);
    Map<String, Object> getAllIssuesWithFilter(String projectKey, JiraTaskFilterDto jiraTaskFilterDto);
    Map<String, Object> getAllIssues(String projectKey);
    Map<String, Object> getIssueById(long issueId);
    Map<String, Object> createIssueWithJSON(Map json);


}