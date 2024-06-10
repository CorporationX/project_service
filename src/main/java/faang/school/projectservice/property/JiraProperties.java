package faang.school.projectservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jira")
public class JiraProperties {
    private String username;
    private String password;
    private String jiraURL;
}
