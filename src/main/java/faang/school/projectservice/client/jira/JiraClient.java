package faang.school.projectservice.client.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class JiraClient {

    @Value("${spring.jira.client.username}")
    private String username;
    @Value("${spring.jira.client.url}")
    private String url;
    @Value("${api-token}")
    private String token;

    @Bean
    public JiraRestClient jiraRestClient() throws URISyntaxException {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(new URI(url), username, token);
    }
}