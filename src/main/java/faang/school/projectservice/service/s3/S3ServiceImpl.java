package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.UploadResourceException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.properties.S3Properties;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        checkBucketExists(s3Properties.getBucketName());
        String key = String.format("%s-%s-%s", LocalDateTime.now(), folder, file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest;
        try {
            putObjectRequest =
                    new PutObjectRequest(s3Properties.getBucketName(), key, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new UploadResourceException("Upload resource is failed");
        }
        s3Client.putObject(putObjectRequest);
        return buildResource(file, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(s3Properties.getBucketName(), key);
        S3Object object = s3Client.getObject(getObjectRequest);
        if (object == null) {
            throw new EntityNotFoundException(String.format("Resource not found by key = %s", key));
        }
        return object.getObjectContent();
    }

    @Override
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3Properties.getBucketName(), key);
        s3Client.deleteObject(deleteObjectRequest);
    }

    private void checkBucketExists(String bucketName) {
        boolean isExistsBucket = s3Client.doesBucketExistV2(bucketName);
        if (!isExistsBucket) {
            s3Client.createBucket(s3Properties.getBucketName());
        }
    }

    private Resource buildResource(MultipartFile file, String key) {
        return Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
