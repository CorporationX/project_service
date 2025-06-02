package faang.school.projectservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("services.s3")
record S3Property(
        String accessKey,
        String secretKey,
        String endpoint,
        String region
) {
}
