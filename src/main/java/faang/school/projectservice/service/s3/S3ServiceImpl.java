package faang.school.projectservice.service.s3;


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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(),file.getOriginalFilename());
        log.info("Starting file upload to S3. Bucket: {}, Key: {}", bucketName, key);
        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata
            );
            s3Client.putObject(putObjectRequest);
            log.info("File uploaded successfully to S3. Key: {}", key);
        } catch (Exception e) {
            log.error("Error occurred while uploading file to S3. Key: {}, Error: {}", key, e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());
        log.info("Resource created for file. Name: {}, Key: {}, Size: {}", resource.getName(), resource.getKey(), resource.getSize());
        return resource;
    }

    @Override
    public void deleteFile(String key) {
        log.info("Deleting file from S3. Key: {}", key);
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("File deleted successfully from S3. Key: {}", key);
        } catch (Exception e) {
            log.error("Error occurred while deleting file from S3. Key: {}, Error: {}", key, e.getMessage());
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        log.info("Downloading file from S3. Key: {}", key);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            log.info("File downloaded successfully from S3. Key: {}", key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Error occurred while downloading file from S3. Key: {}, Error: {}", key, e.getMessage());
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }
}
