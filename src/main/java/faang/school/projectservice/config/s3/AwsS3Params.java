package faang.school.projectservice.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.s3")
@Getter
@Setter
public class AwsS3Params {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
}