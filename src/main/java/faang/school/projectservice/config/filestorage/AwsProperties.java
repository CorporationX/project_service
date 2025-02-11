package faang.school.projectservice.config.filestorage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
