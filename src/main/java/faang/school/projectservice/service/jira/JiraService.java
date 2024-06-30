package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;

import java.util.List;

public interface JiraService {

    String createIssue(IssueDto issueDto);

    IssueDto getIssue(String issueKey);

    List<IssueDto> getAllIssues(String projectKey);

    List<IssueDto> getIssuesByFilter(String projectKey, IssueFilterDto issueFilterDto);
}
