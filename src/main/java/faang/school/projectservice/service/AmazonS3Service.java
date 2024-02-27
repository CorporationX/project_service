package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 s3Client;

    @Value("${s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        String key = getKey(folder, originalFilename);
        ObjectMetadata objectMetadata = getObjectMetadata(fileSize, contentType);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .type(ResourceType.getResourceType(contentType))
                .name(originalFilename)
                .build();
    }

    private String getKey(String folder, String originalFilename) {
        return String.format("%s/%d%s", folder, System.currentTimeMillis(), originalFilename);
    }

    private ObjectMetadata getObjectMetadata(long fileSize, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }

}


