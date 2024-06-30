package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.mapper.jira.IssueTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final UserServiceClient userServiceClient;
    private final JiraClient jiraClient;
    private final IssueMapper issueMapper;
    private final IssueTypeMapper issueTypeMapper;
    private final JiraFilterService jiraFilterService;

    @Override
    public String createIssue(IssueDto issueDto) {

        authorizeUser();

        IssueInput issue = buildInput(issueDto);
        String key = jiraClient.createIssue(issue);

        log.info("Created issue {} for project: {}", key, issueDto.getProjectKey());

        return key;
    }

    @Override
    public IssueDto getIssue(String issueKey) {

        authorizeUser();

        Issue issue = jiraClient.getIssue(issueKey);
        return issueMapper.toDto(issue);
    }

    @Override
    public List<IssueDto> getAllIssues(String projectKey) {

        authorizeUser();

        List<Issue> issues = jiraClient.getAllIssues(projectKey);
        return issues.stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Override
    public List<IssueDto> getIssuesByFilter(String projectKey, IssueFilterDto issueFilterDto) {

        authorizeUser();

        Stream<Issue> allIssues = jiraClient.getAllIssues(projectKey).stream();
        return jiraFilterService.applyAll(allIssues, issueFilterDto)
                .map(issueMapper::toDto)
                .toList();
    }

    public void authorizeUser() {

        JiraAccountDto account = userServiceClient.getJiraAccount();

        jiraClient.setUsername(account.getUsername());
        jiraClient.setPassword(account.getPassword());
        jiraClient.setProjectUrl(account.getProjectUrl());

        JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(account.getProjectUrl()), account.getUsername(), account.getPassword());

        jiraClient.setRestClient(jiraRestClient);
    }

    private IssueInput buildInput(IssueDto issueDto) {
        return new IssueInputBuilder()
                .setProjectKey(issueDto.getProjectKey())
                .setIssueType(issueTypeMapper.toEntity(issueDto.getIssueType()))
                .setDescription(issueDto.getDescription())
                .setSummary(issueDto.getSummary())
                .setDueDate(DateTime.parse(issueDto.getDueDate().toString()))
                .build();
    }
}
