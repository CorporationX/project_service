package faang.school.projectservice.service;

import faang.school.projectservice.exception.ResourceProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void uploadImage(byte[] imageBytes, String key, String contentType) {
        try {
            log.info("Uploading image with key: {} to MinIO bucket: {}", key, bucketName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(imageBytes));

            log.info("Successfully uploaded image with key: {} to MinIO", key);
        } catch (S3Exception e) {
            log.error("Failed to upload image with key: {} to MinIO", key, e);
            throw new ResourceProcessingException("Failed to upload image to MinIO", e);
        }
    }

    public void deleteImage(String key) {
        try {
            log.info("Deleting image with key: {} from MinIO bucket: {}", key, bucketName);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

            log.info("Successfully deleted image with key: {} from MinIO", key);
        } catch (S3Exception e) {
            log.error("Failed to delete image with key: {} from MinIO", key, e);
            throw new ResourceProcessingException("Failed to delete image from MinIO", e);
        }
    }
}
