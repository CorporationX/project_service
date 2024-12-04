package faang.school.projectservice.config.jira;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "jira.search")
public class JiraSearchConfig {
    private List<String> defaultFields;
}
