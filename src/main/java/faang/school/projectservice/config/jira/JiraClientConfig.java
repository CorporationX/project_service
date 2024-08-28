package faang.school.projectservice.config.jira;

//import com.atlassian.jira.rest.client.api.JiraRestClient;
//import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Configuration
//public class JiraClientConfig {
//
//    @Value("${spring.jira.url}")
//    private String jiraUrl;
//
//    @Value("${spring.jira.username}")
//    private String username;
//
//    @Value("${api-token}")
//    private String apiToken;
//
//    @Bean
//    public JiraRestClient jiraRestClient() throws URISyntaxException {
//        return new AsynchronousJiraRestClientFactory()
//                .createWithBasicHttpAuthentication(new URI(jiraUrl), username, apiToken);
//    }
//}
