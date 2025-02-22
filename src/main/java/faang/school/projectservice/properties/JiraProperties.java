package faang.school.projectservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jira")
public class JiraProperties {

    private String url;

    private String projectName;

    private String username;

    private String token;
}
