package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.CloudService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked")
public class S3ServiceImpl implements CloudService {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("IO exception was thrown", e);
            throw new FileException("Error while uploading file %s to bucket %s"
                    .formatted(file.getOriginalFilename(), bucketName));
        }
        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .size(BigInteger.valueOf(fileSize))
                .build();
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            s3Client.deleteObject(bucketName, fileKey);
            log.debug("File with key %s was deleted from bucket %s".formatted(fileKey, bucketName));
        } catch (Exception e) {
            log.error("Unexpected error while deleting file {} from bucket {}", fileKey, bucketName, e);
            throw new FileException("Error while deleting file with key %s from bucket %s"
                    .formatted(fileKey, bucketName));
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Exception was thrown while downloading file", e);
            throw new FileException("Error while downloading file with key %s from bucket %s"
                    .formatted(key, bucketName));
        }
    }

    @PostConstruct
    private void initializeBucket() {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
            log.debug("Bucket %s created successfully".formatted(bucketName));
        }
    }
}