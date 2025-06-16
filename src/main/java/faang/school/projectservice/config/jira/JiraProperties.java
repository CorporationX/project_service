package faang.school.projectservice.config.jira;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jira")
public record JiraProperties(
        @Value("${username}")
        String username,
        @Value("${api-token}")
        String apiToken,
        @Value("${base-url}")
        String baseUrl,
        @Value("${rest-api-url}")
        String restApiUrl,
        @Value("${jql-search}")
        String jqlSearch,
        @Value("${issue}")
        String issue
) {
}
