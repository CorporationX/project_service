package faang.school.projectservice.config.cover;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cover")
@Data
public class CoverConfiguration {
    private Map<String, Section> types = new HashMap<>();

    @Getter
    @Setter
    public static class Section {
        private long maxSizeMB;
        private Integer horizontalWidth;
        private Integer horizontalHeight;
        private Integer squareSide;
        private Integer maxSide;
    }
}
