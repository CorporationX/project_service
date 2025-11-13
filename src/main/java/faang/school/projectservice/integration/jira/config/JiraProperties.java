package faang.school.projectservice.integration.jira.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jira")
public class JiraProperties {

    private SystemConfig system = new SystemConfig();
    private OAuthConfig oauth = new OAuthConfig();
    private CacheConfig cache = new CacheConfig();
    private String projectKey;
    private String issueType = "Task";
    private int timeoutSeconds = 30;
    private int maxRetries = 3;

    @Data
    public static class SystemConfig {
        private String baseUrl;
        private String username;
        private String apiToken;
    }

    @Data
    public static class OAuthConfig {
        private boolean enable = false;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authorizationUri = "https://auth.atlassian.com/authorize";
        private String tokenUri = "https://auth.atlassian.com/oauth/token";
        private String scope = "read:jira-work write:jira-work offline_access";
    }
    
    @Data
    public static class CacheConfig {
        private boolean enabled = true;
        private int issueTtlMinutes = 30;      // TTL для Issues
        private int transitionsTtlMinutes = 15; // TTL для Transitions
        private int projectsTtlHours = 24;     // TTL для Projects
        private int usersTtlHours = 24;        // TTL для Users
    }
}
