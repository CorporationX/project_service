package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
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
import java.util.UUID;

import static faang.school.projectservice.service.s3.S3ErrorMessage.FAILED_DELETE_FILE;
import static faang.school.projectservice.service.s3.S3ErrorMessage.FAILED_UPLOAD_FILE;
import static faang.school.projectservice.service.s3.S3ErrorMessage.NOT_NULL_FILE_KEY;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentLength(fileSize);
        objectMetaData.setContentType(file.getContentType());
        String key = String.format("%s/%s_%s", folder,
                UUID.randomUUID(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetaData);
            s3Client.putObject(putObjectRequest);
        } catch (AmazonS3Exception e) {
            log.error("Failed to upload file to S3: ", e);
            throw new FileException(FAILED_UPLOAD_FILE);
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading file: ", e);
            throw new FileException(FAILED_UPLOAD_FILE);
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resource;
    }

    public void deleteFile(String key) {
        if (key == null || key.isBlank()) {
            throw new FileException(NOT_NULL_FILE_KEY);
        }

        try {
            s3Client.deleteObject(bucketName, key);
        } catch (AmazonS3Exception e) {
            log.error("Failed to delete file from S3: ", e);
            throw new FileException(FAILED_DELETE_FILE);
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting file: ", e);
            throw new FileException(FAILED_DELETE_FILE);
        }
    }
}
