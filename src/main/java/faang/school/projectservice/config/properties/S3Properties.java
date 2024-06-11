package faang.school.projectservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "services.s3")
@Configuration
public class S3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String isMocked;
}
