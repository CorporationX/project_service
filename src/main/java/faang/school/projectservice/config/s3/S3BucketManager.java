package faang.school.projectservice.config.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3BucketManager {
    private final S3Client s3Client;
    private final MinioConfigProperties minioConfigProperties;

    @PostConstruct
    public void checkBucketExists() {
        String bucketname = minioConfigProperties.getBucketName();
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketname).build());
            log.info("Bucket '{}' exists. Continue execution", bucketname);
        } catch (NoSuchBucketException e) {
            log.warn("Bucket '{}' doesn't exist. Creating new bucket", bucketname);
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketname).build());
                log.info("Bucket '{}' successfully created", bucketname);
            } catch (S3Exception bucketCreateException) {
                log.error("Failed to create bucket '{}': {}", bucketname, bucketCreateException.awsErrorDetails().errorMessage());
                throw bucketCreateException;
            }
        } catch (S3Exception s3Exception) {
            throw s3Exception;
        }
    }
}