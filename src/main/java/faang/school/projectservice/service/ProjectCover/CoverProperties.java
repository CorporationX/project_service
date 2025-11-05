package faang.school.projectservice.service.ProjectCover;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cover")
@Getter
public class CoverProperties {

    private long maxSizeBytes;
    private int maxWidthHorizontal;
    private int maxHeightHorizontal;
    private int maxWidthVertical;
    private int maxHeightVertical;
    private int maxSquare;
    private double outputQuality;
}