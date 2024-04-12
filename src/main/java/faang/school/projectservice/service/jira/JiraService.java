package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.mapper.jira.IssueTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraService {
    private final UserServiceClient userServiceClient;
    private final IssueMapper issueMapper;
    private final IssueTypeMapper issueTypeMapper;
    private final JiraClient jiraClient;

    public String createIssue(IssueDto issueDto) {
        authenticateUserInJiraClient();
        IssueInput issue = buildIssueInput(issueDto);
        return jiraClient.createIssue(issue);
    }

    public IssueDto getIssue(String issueKey) {
        authenticateUserInJiraClient();
        return issueMapper.toDto(jiraClient.getIssue(issueKey));
    }

    public List<IssueDto> getAllIssues(String projectKey) {
        authenticateUserInJiraClient();
        List<Issue> issues = jiraClient.getAllIssues(projectKey);
        return issueMapper.toDto(issues);
    }

    public List<IssueDto> getIssuesByStatusId(String projectKey, long statusId) {
        authenticateUserInJiraClient();
        List<Issue> issues = jiraClient.getIssuesByStatusId(projectKey, statusId);
        return issueMapper.toDto(issues);
    }

    public List<IssueDto> getIssuesByAssigneeId(String projectKey, String assigneeId) {
        authenticateUserInJiraClient();
        List<Issue> issues = jiraClient.getIssuesByAssigneeId(projectKey, assigneeId);
        return issueMapper.toDto(issues);
    }

    private void authenticateUserInJiraClient() {
        JiraAccountDto account = userServiceClient.getJiraAccountInfo();
        jiraClient.setUsername(account.getUsername());
        jiraClient.setPassword(account.getPassword());
        jiraClient.setProjectUrl(account.getProjectUrl());
        JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(account.getProjectUrl()), account.getUsername(), account.getPassword());
        jiraClient.setRestClient(jiraRestClient);
        log.info("Authentication for user {} completed", account.getUsername());
    }

    private IssueInput buildIssueInput(IssueDto issueDto) {
        return new IssueInputBuilder()
                .setProjectKey(issueDto.getProjectKey())
                .setIssueType(issueTypeMapper.toEntity(issueDto.getIssueType()))
                .setSummary(issueDto.getSummary())
                .setDescription(issueDto.getDescription())
                .build();
    }
}
