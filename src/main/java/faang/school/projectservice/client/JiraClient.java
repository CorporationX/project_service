package faang.school.projectservice.client;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class JiraClient {
    private String username;
    private String password;
    private String jiraUrl;
    private JiraRestClient restClient;

    public JiraClient(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
        this.restClient = getJiraClient();
    }

    private JiraRestClient getJiraClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraUri(), this.username, this.password);
    }

    private URI getJiraUri() {
        return URI.create(this.jiraUrl);
    }
}
