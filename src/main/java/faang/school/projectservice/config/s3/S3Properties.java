package faang.school.projectservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.s3")
public record S3Properties(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucketName,
        boolean isMocked,
        String region,
        String presentationFolder
) {
}
