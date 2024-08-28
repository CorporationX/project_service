package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource createFile(MultipartFile file, String nameFolder) {
        String key = nameFolder + "/" + file.getOriginalFilename() + file.getSize() + file.getContentType();

        ObjectMetadata objectMetadata = createMetadata(file);
        uploadFileToS3(key, file, objectMetadata);
        return createResourceForFileUploading(file, key);
    }

    public void deleteFile(Resource resource) {
        try {
            s3client.deleteObject(bucketName, resource.getKey());
        } catch (Exception exception) {
            log.error("Failed to delete file to S3. Service: {}, File Name: {}, Error: {}",
                    "AmazonS3Service",
                    resource.getName(),
                    exception.getMessage(),
                    exception);
            throw new IllegalStateException("Failed to delete file from S3", exception);
        }

        resource.setStatus(ResourceStatus.DELETED);
        resource.setKey("");
        resource.setSize(new BigInteger("0"));
    }

    private void uploadFileToS3(String key, MultipartFile file, ObjectMetadata objectMetadata) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3client.putObject(putObjectRequest);
        } catch (Exception exception) {
            log.error("Failed to upload file to S3. Service: {}, File Name: {}, Error: {}",
                    "AmazonS3Service",
                    file.getOriginalFilename(),
                    exception.getMessage(),
                    exception);
            throw new IllegalStateException("Failed to upload file to S3", exception);
        }
    }

    private ObjectMetadata createMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    private Resource createResourceForFileUploading(MultipartFile file, String key) {
        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(file.getOriginalFilename())
                .build();
    }
}
