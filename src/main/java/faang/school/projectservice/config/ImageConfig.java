package faang.school.projectservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "resource.image")
public class ImageConfig {
    private int maxRectangleWidth;
    private int maxRectangleHeight;
    private int maxSquareDimension;
}