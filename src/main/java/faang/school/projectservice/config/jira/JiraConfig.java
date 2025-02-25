package faang.school.projectservice.config.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class JiraConfig {
    @Value("${jira.url}")
    private String jiraUrl;

    @Value("${jira.user}")
    private String jiraUser;

    @Value("${jira.token}")
    private String jiraToken;

    @Bean
    public JiraRestClient jiraRestClient() {
        try {
            URI jiraServerUri = new URI(jiraUrl);
            AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            return factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUser, jiraToken);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating JiraRestClient", e);
        }
    }
}
