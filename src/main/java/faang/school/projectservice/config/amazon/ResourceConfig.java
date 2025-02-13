package faang.school.projectservice.config.amazon;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "resource")
public class ResourceConfig {
    private long maxSize;
    private Image image;

    @Getter
    @Setter
    public static class Image {
        private int maxRectangleWidth;
        private int maxRectangleHeight;
        private int maxSquareDimension;
    }
}
