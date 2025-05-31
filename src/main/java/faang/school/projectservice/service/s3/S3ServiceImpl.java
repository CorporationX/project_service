package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String projectName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String key = String.format("%s/%s", projectName, file.getOriginalFilename());
        try {
            PutObjectRequest savedFile = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(savedFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .build();
    }

    @Override
    public InputStream downloadFile(String fileKey) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Exception was thrown while downloading file", e);
            throw new FileException("Error while downloading file with key %s from bucket %s"
                    .formatted(fileKey, bucketName));
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            amazonS3.deleteObject(bucketName, fileKey);
            log.debug("File with key %s was deleted from bucket %s".formatted(fileKey, bucketName));
        } catch (Exception e) {
            log.error("Unexpected error while deleting file {} from bucket {}", fileKey, bucketName, e);
            throw new FileException("Error while deleting file with key %s from bucket %s"
                    .formatted(fileKey, bucketName));
        }
    }

    @PostConstruct
    private void initializeBucket() {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
            log.debug("Bucket %s created successfully".formatted(bucketName));
        }
    }
}