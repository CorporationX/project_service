package faang.school.projectservice.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "image")
public record ImageProperty(
        @DefaultValue("512.0") double maxSideSizePx,
        @DefaultValue("1.0") double quality
) {
}
