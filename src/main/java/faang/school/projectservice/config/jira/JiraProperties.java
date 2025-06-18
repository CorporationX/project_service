package faang.school.projectservice.config.jira;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jira")
public record JiraProperties(
        @Value("${username}")
        String username,
        @Value("${api-token}")
        String apiToken,
        @Value("${jql-search}")
        String jqlSearch,
        @Value("${issue}")
        String issue,
        @Value("${jira-rest-api-base-url}")
        String jiraRestApiBaseUrl,
        @Value("${startAt}")
        String defaultStartAt,
        @Value("${maxResults}")
        String defaultMaxResults
) {
}
