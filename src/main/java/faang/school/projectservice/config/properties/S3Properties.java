package faang.school.projectservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.s3")
public record S3Properties(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucketName,
        String region
) {
}
