package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void putFileInStore(String key, InputStream stream, ObjectMetadata metadata) {
        try {
            s3Client.putObject(bucketName, key, stream, metadata);
        } catch (Exception e) {
            log.error("Ошибка при сохранении файла в S3", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void putFileInStore(String key, InputStream stream) {
        putFileInStore(key, stream, null);
    }

    public Resource uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .build();
    }

    public InputStream downloadFile(String key) {
        S3Object s3Object = s3Client.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
