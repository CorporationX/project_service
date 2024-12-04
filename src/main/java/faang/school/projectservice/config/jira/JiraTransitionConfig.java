package faang.school.projectservice.config.jira;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "jira.transition")
@Data
public class JiraTransitionConfig {
    private Map<String, String> ids;
}
