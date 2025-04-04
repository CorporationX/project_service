package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.config.properties.S3Properties;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
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
}
