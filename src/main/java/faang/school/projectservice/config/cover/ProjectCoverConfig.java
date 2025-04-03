package faang.school.projectservice.config.cover;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "project-cover")
public class ProjectCoverConfig {
    private long maxSizeMB;
    private int horizontalWidth;
    private int horizontalHeight;
    private int squareSide;
}
