package faang.school.projectservice.service;

import faang.school.projectservice.config.AppConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final AppConfig appConfig;

    public void uploadFile(ByteArrayInputStream fileInputStream, String prefix, String contentType, Long fileSize) {
        PutObjectArgs args = PutObjectArgs.builder()
                .contentType(contentType)
                .object(prefix)
                .bucket(appConfig.getMinioBucketName())
                .stream(fileInputStream, fileSize, -1)
                .build();
        try {
            minioClient.putObject(args);
        } catch (Exception e) {
            log.error("Failed to minioClient.putObject(args)", e);
            throw new RuntimeException("Failed to put object (minio)");
        }
    }

    public void removeFile(String key) {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(appConfig.getMinioBucketName())
                .object(key)
                .build();
        try {
            minioClient.removeObject(args);
        } catch (Exception e) {
            log.error("Failed to minioClient.removeObject(args)", e);
            throw new RuntimeException("Failed to remove object (minio)");
        }
    }
}
