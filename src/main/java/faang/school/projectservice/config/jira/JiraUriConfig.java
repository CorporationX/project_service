package faang.school.projectservice.config.jira;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jira.api")
@Getter
@Setter
public class JiraUriConfig {
    private String createIssueUri;
    private String updateIssueUri;
    private String transitionIssueUri;
    private String searchUri;
}
