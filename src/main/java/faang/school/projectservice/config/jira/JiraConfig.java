package faang.school.projectservice.config.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import faang.school.projectservice.property.JiraProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class JiraConfig {

    private final JiraProperties jiraProperties;

    @Bean
    public JiraRestClient jiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(
                        URI.create(jiraProperties.getJiraURL()),
                        jiraProperties.getUsername(),
                        jiraProperties.getPassword()
                );
    }
}
