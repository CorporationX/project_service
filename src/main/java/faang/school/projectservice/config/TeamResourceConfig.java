package faang.school.projectservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "team.resource.avatar")
@Data
public class TeamResourceConfig {

    private long maxSize;
    private int width;
    private int height;
    private Set<String> supportedContentTypes;
}
