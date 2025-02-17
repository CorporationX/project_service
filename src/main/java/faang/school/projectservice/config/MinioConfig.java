package faang.school.projectservice.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final AppConfig appConfig;
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(appConfig.getMinioEndpoint())
                .credentials(appConfig.getMinioLogin(), appConfig.getMinioPassword())
                .build();
    }
}
