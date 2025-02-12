package faang.school.projectservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${services.minio.endpoint}") String endpoint,
            @Value("${services.minio.access-key}") String accessKey,
            @Value("${services.minio.secret-key}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Value("${services.minio.bucket-name}")
    private String bucketName;

    @Bean
    public String minioBucketName() {
        return bucketName;
    }
}
