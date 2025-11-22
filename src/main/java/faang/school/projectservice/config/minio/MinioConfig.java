package faang.school.projectservice.config.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.region:us-east-1}")
    private String region;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            createBucketIfExist(minioClient);

            log.info("MinIO client configured successfully");
            return minioClient;

        } catch (Exception e) {
            log.error("Error configuring MinIO client", e);
            throw new RuntimeException("Failed to configure MinIO client", e);
        }

    }

    private void createBucketIfExist(MinioClient minioClient) {
        try {
            boolean exist = minioClient.bucketExists(
                    BucketExistsArgs
                            .builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exist) {
                minioClient.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(bucketName)
                                .region(region)
                                .build()
                );
                log.info("Created MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Error creating bucket", e);
            throw new RuntimeException("Failed to create bucket", e);
        }
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}