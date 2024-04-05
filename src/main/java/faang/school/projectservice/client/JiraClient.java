package faang.school.projectservice.client;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class JiraClient {
    private String username;
    private String password;
    private String projectUrl;
    private JiraRestClient restClient;

    public JiraClient(JiraAccountDto jiraAccountDto) {
        this.username = jiraAccountDto.getUsername();
        this.password = jiraAccountDto.getPassword();
        this.projectUrl = jiraAccountDto.getProjectUrl();
        this.restClient = getJiraClient();
    }

    public String createIssue(IssueInput issue) throws ExecutionException, InterruptedException, TimeoutException {
        IssueRestClient issueRestClient = restClient.getIssueClient();
        return issueRestClient.createIssue(issue).get(20, TimeUnit.SECONDS).getKey();
    }

    private JiraRestClient getJiraClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getProjectUri(), this.username, this.password);
    }

    private URI getProjectUri() {
        return URI.create(this.projectUrl);
    }
}
