package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;

import java.util.List;

public interface JiraService {

    ProjectDto registrationProjectInJira(long id, String jiraKey);

    IssueDto createIssue(IssueDto issueDto);

    void updateIssueByKey(String issueKey, IssueUpdateDto issueDto);

    List<IssueDto> getAllIssuesByProjectId(long projectId);

    IssueDto getIssueByKey(String issueKey);

    List<IssueDto> getAllIssuesWithFilterByProjectId(long projectKey, IssueFilterDto filter);
}
