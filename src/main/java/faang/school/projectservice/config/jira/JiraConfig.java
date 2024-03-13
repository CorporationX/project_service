//package faang.school.projectservice.config.jira;
//
//
//import com.atlassian.jira.rest.client.api.JiraRestClient;
//import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.net.URI;
//
//@Configuration
//public class JiraConfig {
//
//    String jiraUrl = "https://atlassian-jira-integration-test.atlassian.net";
//    String password = "ATATT3xFfGF0iTo8KNsID1lHyJmQmQ26Nj6AcPxeAj_4simiq7wM7xFkAeucbHEC0qmIwWwGrebrelI9qyrLbtA7QG7kRDRbHj2o0ZiZjMKEKYAIXpmAcqJgoMBtHjjq4z95xQm8S0FevbOgmTdcWTdqq9NHMIHjREeJgZ94gGV-y4ryfWhH1OI=3813B801";
//    String username = "atlassian.jira.integration@gmail.com";
//
//    @Bean
//    public JiraRestClient getJiraRestClient() {
//        return new AsynchronousJiraRestClientFactory()
//                .createWithBasicHttpAuthentication(getJiraUri(), username, password);
//    }
//
//
//    public URI getJiraUri() {
//        return URI.create(this.jiraUrl);
//    }
//
//}
