package faang.school.projectservice.config.cover;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "project-cover")
@Getter
@Setter
public class ProjectCoverConfig {
    private long maxSizeMB;
    private int horizontalWidth;
    private int horizontalHeight;
    private int squareSide;
}
