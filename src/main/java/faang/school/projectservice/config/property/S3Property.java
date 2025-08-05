package faang.school.projectservice.config.property;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "services.s3")
public record S3Property(
        @NonNull String endpoint,
        @NonNull String accessKey,
        @NonNull String secretKey,
        @DefaultValue("projectbucket")
        String bucketName,
        @DefaultValue("us-east-1")
        String region) {
}
