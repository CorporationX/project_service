package faang.school.projectservice.config.cover;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cover.types.project")
@Data
public class ProjectCoverConfiguration implements CoverConfiguration {
    private long maxSizeMB;
    private Integer horizontalWidth;
    private Integer horizontalHeight;
    private Integer squareSide;
}
