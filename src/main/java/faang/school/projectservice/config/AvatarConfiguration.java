package faang.school.projectservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "picture-provider")
public class AvatarConfiguration {
    private int imageLimitSize;
    private int imageLimit;
}
