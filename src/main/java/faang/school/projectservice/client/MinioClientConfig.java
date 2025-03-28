package faang.school.projectservice.client;

import faang.school.projectservice.exception.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioClientConfig {
    private static final String ERROR_DURING_CREATION_BUCKET_WITH_NAME = "Error during creation bucket with name ";

    @Value("${services.minio.endpoint}")
    private String endpoint;

    @Value("${services.minio.access-key}")
    private String accessKey;

    @Value("${services.minio.secret-key}")
    private String secretKey;

    @Value("${services.minio.cover.bucket}")
    private String coverBucketName;

    MinioClient minioClient;

    @Bean
    public MinioClient minioClient() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        createBucketIfNotExists(coverBucketName);
        return minioClient;
    }

    public void createBucketIfNotExists(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error(ERROR_DURING_CREATION_BUCKET_WITH_NAME + bucketName);
            throw new StorageException(ERROR_DURING_CREATION_BUCKET_WITH_NAME + bucketName);
        }
    }
}
