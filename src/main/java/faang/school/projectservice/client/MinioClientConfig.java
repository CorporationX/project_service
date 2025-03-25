package faang.school.projectservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfig {
    @Value("${services.minio.endpoint}")
    private String endpoint;

    @Value("${services.minio.access-key}")
    private String accessKey;

    @Value("${services.minio.secret-key}")
    private String secretKey;

    @Bean
    public io.minio.MinioClient minioClient() {
        return io.minio.MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
