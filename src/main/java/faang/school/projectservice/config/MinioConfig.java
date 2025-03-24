package faang.school.projectservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${app.minio.presentations.endpoint}")
    private String endpoint;

    @Value("${app.minio.presentations.access-key}")
    private String accessKey;

    @Value("${app.minio.presentations.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient
                .builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
