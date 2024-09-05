package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest request = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(request);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed uploading file: " + file.getOriginalFilename());
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));

        return resource;
    }

    public InputStream downloadResource(String key) {
        try {
            S3Object object = amazonS3.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed download file");
        }
    }

    public String putIntoBucket(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = getMetadata(multipartFile);

        try {
            String key = System.currentTimeMillis() + multipartFile.getOriginalFilename();
            PutObjectRequest request = new PutObjectRequest(bucketName, key,
                    multipartFile.getInputStream(), objectMetadata);
            amazonS3.putObject(request);

            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed uploading file: " + multipartFile.getOriginalFilename());
        }
    }

    private ObjectMetadata getMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        return objectMetadata;
    }

    public void deleteFromBucket(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteObjectRequest);
    }
}
