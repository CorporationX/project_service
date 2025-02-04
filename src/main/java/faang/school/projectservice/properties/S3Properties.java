package faang.school.projectservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services.s3")
@Data
public class S3Properties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
