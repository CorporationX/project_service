package faang.school.projectservice.client.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import faang.school.projectservice.config.context.jira.JiraAuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

@RequiredArgsConstructor
@Component
public class JiraClient {
    private final JiraAuthContext jiraAuthContext;

    public JiraRestClient getJiraRestClient() {
        String username = jiraAuthContext.getUsername();
        String password = jiraAuthContext.getPassword();
        String baseUrl = jiraAuthContext.getBaseUrl();

        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(baseUrl), username, password);
    }

    public IssueRestClient getIssueClient() {
        return getJiraRestClient().getIssueClient();
    }

    public SearchRestClient getSearchClient() {
        return getJiraRestClient().getSearchClient();
    }
}
