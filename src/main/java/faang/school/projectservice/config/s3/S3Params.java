package faang.school.projectservice.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "services.s3")
public class S3Params {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
}