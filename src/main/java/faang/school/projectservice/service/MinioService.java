package faang.school.projectservice.service;

import faang.school.projectservice.config.minio.properties.MinioProperties;
import faang.school.projectservice.exception.MinioException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    @PostConstruct
    public void createBucketIfItDoesNotExists() {
        try {
            String bucketName = minioProperties.getBucketName();

            boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!isBucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());

                log.info("MinIO bucket '" + bucketName + "' created successfully");
            }
        } catch (Exception e) {
            log.error("An error occurred when creating the MinIO bucket: ", e);
        }
    }

    public void uploadFile(String fileKey, InputStream processedStream, long fileSize, String fileContentType) {
        try {
            long processedSize = processedStream.available();

            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Length", Long.toString(fileSize));
            metadata.put("Content-Type", fileContentType);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileKey)
                            .stream(processedStream, processedSize, -1)
                            .userMetadata(metadata)
                            .contentType(fileContentType)
                            .build()
            );

            log.info("File uploaded to MinIO: " + fileKey);
        } catch (Exception e) {
            log.error("Error uploading file to MinIO: ", e);
            throw new MinioException("Error uploading file to MinIO: " + e.getMessage());
        }
    }

    public InputStream getFile(String fileKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileKey)
                    .build());
        } catch (Exception e) {
            throw new MinioException("Error receiving file from MinIO: " + e.getMessage());
        }
    }

    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileKey)
                    .build());

            log.info("File " + fileKey + " has been deleted from MinIO");
        } catch (Exception e) {
            throw new MinioException("Error deleting file from MinIO: " + e.getMessage());
        }
    }
}
