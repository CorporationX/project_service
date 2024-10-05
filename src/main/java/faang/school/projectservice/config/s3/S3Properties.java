package faang.school.projectservice.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "services.s3")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
}