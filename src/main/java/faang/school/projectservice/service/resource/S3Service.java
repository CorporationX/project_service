package faang.school.projectservice.service.resource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.MinioUploadException;
import faang.school.projectservice.model.entity.Resource;
import faang.school.projectservice.model.enums.ResourceStatus;
import faang.school.projectservice.model.enums.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 s3Client;

    @Setter
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String uniqueID = UUID.randomUUID().toString();
        String key = String.format("%s/%s%s", folder, uniqueID, file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("Something wrong: ", e);
            throw new RuntimeException(e);
        }
        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setKey(key);
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resource;
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    public S3Object getFile(String key) {
        return s3Client.getObject(bucketName, key);
    }

    public String uploadCoverImage(String key, InputStream inputStream, String contentType, long contentLength) throws MinioUploadException {
        log.info("Загрузка файла в Minio: key={}, contentType={}, contentLength={}", key, contentType, contentLength);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjectRequest);
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

    public InputStream downloadCoverImage(String key) {
        log.info("Загрузка файла из Minio: key={}", key);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            log.info("Файл успешно загружен из Minio: key={}", key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла из Minio: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Ошибка при загрузке файла из Minio", e);
        }
    }

    public void deleteCoverImage(String key) {
        log.info("Удаление файла из Minio: key={}", key);
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Файл успешно удален из Minio: key={}", key);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла из Minio: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении файла из Minio", e);
        }
    }
}
