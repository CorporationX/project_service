package faang.school.projectservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.s3")
public record AppS3Config(
    String endpoint,
    String accessKey,
    String secretKey 
) {

}
