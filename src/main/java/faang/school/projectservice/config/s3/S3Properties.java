package faang.school.projectservice.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "services.s3")
@Component
@Data
public class S3Properties {
    private String endpoint;
    private String accessKey;
    private String bucketName;
    private String secretKey;
}
