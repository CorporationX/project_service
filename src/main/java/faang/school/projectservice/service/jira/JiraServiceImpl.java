package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.mapper.jira.IssueTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraRestClient restClient;
    private final IssueMapper issueMapper;
    private final IssueTypeMapper issueTypeMapper;

    @Override
    public String createIssue(IssueDto issueDto) {

        IssueRestClient client = restClient.getIssueClient();
        IssueInput issue = buildInput(issueDto);
        String key = client.createIssue(issue).claim().getKey();

        log.info("Creating issue for project: {}", issueDto.getProjectKey());

        return key;
    }

    @Override
    public IssueDto getIssue(String issueKey) {
        IssueRestClient client = restClient.getIssueClient();
        Issue issue = client.getIssue(issueKey).claim();
        return issueMapper.toDto(issue);
    }

    @Override
    public List<IssueDto> getAllIssues(String projectKey) {

        SearchRestClient client = restClient.getSearchClient();
        Iterable<Issue> issues = client.searchJql("project = " + projectKey).claim().getIssues();

        return StreamSupport.stream(issues.spliterator(), false)
                .map(issueMapper::toDto)
                .toList();
    }

    @Override
    public List<IssueDto> getIssuesByStatusId(String projectKey, long statusId) {

        SearchRestClient client = restClient.getSearchClient();
        Iterable<Issue> issues = client.searchJql(String.format("project = %s AND status = %d",
                projectKey, statusId)).claim().getIssues();

        return StreamSupport.stream(issues.spliterator(), false)
                .map(issueMapper::toDto)
                .toList();
    }

    @Override
    public List<IssueDto> getIssuesByAssigneeId(String projectKey, String assigneeId) {

        SearchRestClient client = restClient.getSearchClient();
        Iterable<Issue> issues = client.searchJql(String.format("project = %s AND assignee = %s",
                projectKey, assigneeId)).claim().getIssues();

        return StreamSupport.stream(issues.spliterator(), false)
                .map(issueMapper::toDto)
                .toList();
    }

    private IssueInput buildInput(IssueDto issueDto) {
        return new IssueInputBuilder()
                .setProjectKey(issueDto.getProjectKey())
                .setIssueType(issueTypeMapper.toEntity(issueDto.getIssueType()))
                .setDescription(issueDto.getDescription())
                .setSummary(issueDto.getSummary())
                .setDueDate(issueDto.getDueDate().toDateTime())
                .build();
    }
}
