package faang.school.projectservice.config.minio;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.ConnectException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;
    private final Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioProperties.getEndpoint())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();

            createBucketIfNotExist(minioClient);

            log.info("MinIO client configured successfully");
            return minioClient;

        } catch (Exception e) {
           
            if (isTestProfile() && e.getCause() instanceof ConnectException) {
                log.warn("MinIO is not available in test environment. Mock bean will be used instead.");
                throw new RuntimeException("MinIO connection failed in test environment", e);
            }
            log.error("Error configuring MinIO client", e);
            throw new RuntimeException("Failed to configure MinIO client", e);
        }

    }

    private void createBucketIfNotExist(MinioClient minioClient) {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(minioProperties.getBucketName())
                            .region(minioProperties.getRegion())
                            .build()
            );
            log.info("Created MinIO bucket: {}", minioProperties.getBucketName());
        } catch (ErrorResponseException e) {
            String errorCode = e.errorResponse().code();
            if ("BucketAlreadyOwnedByYou".equals(errorCode) || "BucketAlreadyExists".equals(errorCode)) {
                log.debug("MinIO bucket {} already exists", minioProperties.getBucketName());
            } else {
                log.error("Error creating bucket: {}", errorCode, e);
                throw new RuntimeException("Failed to create bucket", e);
            }
        } catch (Exception e) {
            if (isTestProfile() && e.getCause() instanceof ConnectException) {
                log.warn("Cannot create MinIO bucket in test environment. MinIO is not available.");
                return;
            }
            log.error("Error creating bucket", e);
            throw new RuntimeException("Failed to create bucket", e);
        }
    }

    private boolean isTestProfile() {
        if (environment == null) {
            return false;
        }
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.contains("test")) {
                return true;
            }
        }
        return false;
    }
}