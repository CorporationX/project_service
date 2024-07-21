package faang.school.projectservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "project")
public class ProjectCoverProperties {
    private int maxWidth;
    private String imageExtension;
}
