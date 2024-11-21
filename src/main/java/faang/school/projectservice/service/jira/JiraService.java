package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;

import java.util.List;

public interface JiraService {

    IssueDto createIssue(String projectKey, IssueDto issueDto);

    List<IssueDto> getAllIssueByFilter(IssueFilterDto filter);

    IssueDto getIssue(String issueKey);
}
