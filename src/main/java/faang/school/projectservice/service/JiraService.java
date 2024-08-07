package faang.school.projectservice.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JiraService {

    private final JiraRestClient jiraRestClient;

    public String createIssue(String projectKey, Long issueType, String issueSummary) {
        IssueInput newIssue = new IssueInputBuilder(
                projectKey, issueType, issueSummary).build();
        return jiraRestClient.getIssueClient().createIssue(newIssue).claim().getKey();
    }

    public Issue getIssue(String issueKey) {
        return jiraRestClient.getIssueClient().getIssue(issueKey).claim();
    }

    public void updateIssue(String issueKey, IssueInput issueInput) {
        jiraRestClient.getIssueClient().updateIssue(issueKey, issueInput).claim();
    }

    public Iterable<Issue> searchIssues(String jql) {
        return jiraRestClient.getSearchClient().searchJql(jql).claim().getIssues();
    }
}
