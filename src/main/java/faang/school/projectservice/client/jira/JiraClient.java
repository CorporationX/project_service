package faang.school.projectservice.client.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class JiraClient {

    private String username;
    private String password;
    private String projectUrl;
    private JiraRestClient restClient;

    public String createIssue(IssueInput issue) {
        IssueRestClient client = restClient.getIssueClient();
        return client.createIssue(issue).claim().getKey();
    }

    public Issue getIssue(String issueKey) {
        IssueRestClient client = restClient.getIssueClient();
        return client.getIssue(issueKey).claim();
    }

    public List<Issue> getAllIssues(String projectKey) {
        SearchRestClient client = restClient.getSearchClient();
        Iterable<Issue> issues = client.searchJql("project = " + projectKey).claim().getIssues();

        return StreamSupport.stream(issues.spliterator(), false).toList();
    }

    public List<Issue> getIssuesByStatusId(String projectKey, long statusId) {

        SearchRestClient client = restClient.getSearchClient();
        Iterable<Issue> issues = client.searchJql(String.format("project = %s AND status = %d",
                projectKey, statusId)).claim().getIssues();

        return StreamSupport.stream(issues.spliterator(), false).toList();
    }

    public List<Issue> getIssuesByAssigneeId(String projectKey, String assigneeId) {

        SearchRestClient client = restClient.getSearchClient();
        Iterable<Issue> issues = client.searchJql(String.format("project = %s AND assignee = %s",
                projectKey, assigneeId)).claim().getIssues();

        return StreamSupport.stream(issues.spliterator(), false).toList();
    }
}
