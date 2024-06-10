package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.IssueDto;

import java.util.List;

public interface JiraService {

    String createIssue(IssueDto issueDto);

    IssueDto getIssue(String issueKey);

    List<IssueDto> getAllIssues(String projectKey);

    List<IssueDto> getIssuesByStatusId(String projectKey, long statusId);

    List<IssueDto> getIssuesByAssigneeId(String projectKey, String assigneeId);
}
