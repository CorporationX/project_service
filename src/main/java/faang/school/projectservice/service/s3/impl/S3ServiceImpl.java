package faang.school.projectservice.service.s3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.dto.resource.ResourceObjectResponse;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(byte[] fileContent, String contentType, String fileKey) {
        if (fileContent.length == 0) {
            throw new IllegalArgumentException("File content is empty for key " + fileKey);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(fileContent.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                fileKey,
                new ByteArrayInputStream(fileContent),
                metadata
        );

        s3Client.putObject(putObjectRequest);
    }

    @Override
    public void deleteFile(String fileKey) {
        s3Client.deleteObject(bucketName, fileKey);
    }

    @Override
    public ResourceObjectResponse downloadFile(String fileKey) {
        return null;
    }
}