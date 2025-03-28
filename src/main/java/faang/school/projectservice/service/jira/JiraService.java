package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.ProjectResponseDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;

import java.util.List;

public interface JiraService {
    IssueCreateResponseDto createIssue(IssueRequestDto issueRequestDto);

    void updateIssue(String key, IssueUpdateDto issueUpdateDto);

    List<IssueResponseDto> getAllIssuesWithFilter(Long projectId, IssueFilterDto issueFilterDto);

    List<IssueResponseDto> getAllIssuesByProject(Long projectId);

    IssueResponseDto getIssueByKey(String key);

    ProjectResponseDto registerProject(Long id, String key);
}
