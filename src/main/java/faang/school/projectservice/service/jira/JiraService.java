package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.mapper.jira.IssueTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraService {
    private final UserServiceClient userServiceClient;
    private final IssueMapper issueMapper;
    private final IssueTypeMapper issueTypeMapper;

    public String createIssue(IssueDto issueDto) {
        JiraAccountDto account = getJiraAccFromUserService();
        JiraClient jiraClient = new JiraClient(account);
        IssueInput issue = new IssueInputBuilder()
                .setProjectKey(issueDto.getProjectKey())
                .setIssueType(issueTypeMapper.toEntity(issueDto.getIssueType()))
                .setSummary(issueDto.getSummary())
                .setDescription(issueDto.getDescription())
                .build();
        return jiraClient.createIssue(issue);
    }

    public IssueDto getIssue(String issueKey) {
        JiraAccountDto account = getJiraAccFromUserService();
        JiraClient jiraClient = new JiraClient(account);
        return issueMapper.toDto(jiraClient.getIssue(issueKey));
    }

    public List<IssueDto> getAllIssues(String projectKey) {
        JiraAccountDto account = getJiraAccFromUserService();
        JiraClient jiraClient = new JiraClient(account);
        List<Issue> issues = jiraClient.getAllIssues(projectKey);
        return issueMapper.toDto(issues);
    }

    private JiraAccountDto getJiraAccFromUserService() {
        return userServiceClient.getJiraAccountInfo();
    }
}
