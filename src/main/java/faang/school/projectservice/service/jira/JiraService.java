package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.mapper.jira.IssueTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraService {
    @Value("${jira.PROJECT_URL}")
    private String projectUrl;
    private final JiraClient jiraClient;
    private final IssueMapper issueMapper;
    private final IssueTypeMapper issueTypeMapper;

    public List<IssueDto> getAllIssues(JiraAccountDto jiraAccountDto, String projectKey) {
        userIdentification(jiraAccountDto);
        List<Issue> issues = jiraClient.getAllIssues(projectKey);
        return issues.stream().map(issueMapper::toDto).toList();
    }

    public IssueDto getIssueBiKey(JiraAccountDto jiraAccountDto, String issueKey) {
        userIdentification(jiraAccountDto);
        return issueMapper.toDto(jiraClient.getIssue(issueKey));
    }

    public List<IssueDto> getIssueWithFilterByStatus(JiraAccountDto jiraAccountDto, String projectKey, Long statusId) {
        userIdentification(jiraAccountDto);
        return issueMapper.toDto(jiraClient.getIssueWithFilterByStatus(projectKey, statusId));
    }

    public List<IssueDto> getIssueByExecutorId(JiraAccountDto jiraAccountDto, String projectKey, String executorId) {
        userIdentification(jiraAccountDto);
        return issueMapper.toDto(jiraClient.getIssueByExecutorId(projectKey, executorId));
    }

    public String createIssue(JiraAccountDto jiraAccountDto, IssueDto issueDto) {
        userIdentification(jiraAccountDto);
        IssueInput issue = createIssueInput(issueDto);
        return jiraClient.createIssue(issue);
    }

    private IssueInput createIssueInput(IssueDto issueDto) {
        return new IssueInputBuilder()
                .setProjectKey(issueDto.getProjectKey())
                .setIssueType(issueTypeMapper.toEntity(issueDto.getIssueType()))
                .setSummary(issueDto.getSummary())
                .setDescription(issueDto.getDescription())
                .build();
    }

    private void userIdentification(JiraAccountDto jiraAccountDto) {
        jiraClient.setUsername(jiraAccountDto.getUsername());
        jiraClient.setPassword(jiraAccountDto.getPassword());
        jiraClient.setProjectUrl(projectUrl);

        JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create(projectUrl), jiraAccountDto.getUsername(), jiraAccountDto.getPassword());
        jiraClient.setRestClient(jiraRestClient);
        log.info("User authentication {} completed", jiraAccountDto.getUsername());
    }
}
