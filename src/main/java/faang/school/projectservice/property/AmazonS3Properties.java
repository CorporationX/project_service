package faang.school.projectservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

@Data
@Configuration
@ConfigurationProperties(prefix = "services.s3")
public class AmazonS3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private BigInteger maxFreeStorageSizeBytes;
}
