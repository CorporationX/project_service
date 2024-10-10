package faang.school.projectservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.MinioUploadException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Service {
    private final AmazonS3 amazonS3Client;

    @Setter
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String key, InputStream inputStream, String contentType, long contentLength) throws MinioUploadException {
        log.info("Загрузка файла в Minio: key={}, contentType={}, contentLength={}", key, contentType, contentLength);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3Client.putObject(putObjectRequest);
            log.info("Файл успешно загружен в Minio: key={}", key);
            return key;
        } catch (AmazonServiceException e) {
            log.error("Ошибка при загрузке файла в Minio: {}, error={}", key, e.getMessage(), e);
            throw new MinioUploadException("Ошибка при загрузке файла в Minio", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при загрузке файла в Minio: {}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Неожиданная ошибка при загрузке файла", e);
        }
    }

    public InputStream downloadFile(String key) {
        log.info("Загрузка файла из Minio: key={}", key);
        try {
            S3Object s3Object = amazonS3Client.getObject(bucketName, key);
            log.info("Файл успешно загружен из Minio: key={}", key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла из Minio: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Ошибка при загрузке файла из Minio", e);
        }
    }

    public void deleteFile(String key) {
        log.info("Удаление файла из Minio: key={}", key);
        try {
            amazonS3Client.deleteObject(bucketName, key);
            log.info("Файл успешно удален из Minio: key={}", key);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла из Minio: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении файла из Minio", e);
        }
    }
}