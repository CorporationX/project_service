package faang.school.projectservice.client;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@RequiredArgsConstructor
@Component
public class JiraClient {
    @Value("${services.jira.username}")
    private String username;

    @Value("${services.jira.password}")
    private String password;

    @Value("${services.jira.jira-url}")
    private String jiraUrl;

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraUri(), username, password);
    }

    private URI getJiraUri() {
        return URI.create(jiraUrl);
    }
}