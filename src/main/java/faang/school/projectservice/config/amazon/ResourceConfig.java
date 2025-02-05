package faang.school.projectservice.config.amazon;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "resource")
public class ResourceConfig {
    private long maxSize;
    private Image image = new Image();

    @Getter
    @Setter
    public static class Image {
        private int maxRectangleWidth;
        private int maxRectangleHeight;
        private int maxSquareDimension;
    }
}
