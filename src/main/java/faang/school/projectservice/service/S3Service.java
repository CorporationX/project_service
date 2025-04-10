package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.config.properties.S3Properties;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;

    public Resource uploadFromBytes(byte[] fileBytes, String folder, String fileName, String contentType) {
        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        long fileSize = fileBytes.length;
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), fileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    s3Properties.bucketName(), key, inputStream, metadata
            );
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new FileProcessingException(ExceptionMessage.S3_UPLOAD);
        }

        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(contentType))
                .name(fileName)
                .build();
    }

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    s3Properties.bucketName(), key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(putObjectRequest);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("error uploading file");
        }
        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .build();
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(new DeleteObjectRequest(s3Properties.bucketName(), key));
    }
}
