package faang.school.projectservice.config.jira;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "jira.issue-type")
@Data
public class JiraIssueStatusConfig {
    private Map<String, String> ids;
}
