package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements faang.school.projectservice.service.S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String projectName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String key = String.format("%s/%s", projectName, file.getName());
        try {
            PutObjectRequest savedFile = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(savedFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        return Resource.builder()
                .key(key)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public URL getFileUrl(String fileKey) {
        return amazonS3.getUrl(bucketName, fileKey);
    }

    @Override
    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }
}
