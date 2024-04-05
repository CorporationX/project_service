package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.exception.JiraConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraService {
    private final UserServiceClient userServiceClient;

    public String createIssue(IssueDto issueDto) {
        JiraAccountDto account = userServiceClient.getJiraAccountInfo();
        JiraClient jiraClient = new JiraClient(account);
        IssueInput issue = new IssueInputBuilder()
                .setProjectKey(issueDto.getProjectKey())
                .setIssueType(issueDto.getIssueType())
                .setSummary(issueDto.getSummary())
                .setDescription(issueDto.getDescription())
                .build();

        try {
            return jiraClient.createIssue(issue);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Jira client threw an exception", e);
            Thread.currentThread().interrupt();
            throw new JiraConnectionException(e.getMessage());
        }
    }
}
