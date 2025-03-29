package faang.school.projectservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.s3")
public record S3Properties(String accessKey, String secretKey, String bucketName, String endpoint) {
}
