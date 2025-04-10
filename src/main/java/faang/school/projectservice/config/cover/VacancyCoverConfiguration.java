package faang.school.projectservice.config.cover;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cover.types.vacancy")
@Data
public class VacancyCoverConfiguration implements CoverConfiguration {
    private long maxSizeMB;
    private Integer maxSide;
}
