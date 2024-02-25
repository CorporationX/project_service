package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    private final CoverHandler coverHandler;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            InputStream fileStream = coverHandler.checkCoverAndResize(file);
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, fileStream, objectMetadata);
            ObjectMetadata metadata = putObjectRequest.getMetadata();
            metadata.setContentLength(fileStream.available());
            metadata.setContentType("image/png");
            putObjectRequest.setMetadata(metadata);
            fileStream.close();
            s3Client.putObject(putObjectRequest);
        } catch (AmazonS3Exception | IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
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

    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        try {
            S3Object s3Object = s3Client.getObject(getObjectRequest);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new FileException("Ошибка при загрузке файла: " + e.getMessage());
        }
    }
}

