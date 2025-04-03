package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

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
        String key = String.format("%s%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("Error uploading file to S3. Bucket: {}, Key: {}, Error: {}", bucketName, key, e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setAllowedRoles(List.of(TeamRole.MANAGER));
        resource.setType(ResourceType.IMAGE);
        resource.setStatus(ResourceStatus.ACTIVE);
        return resource;
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
    }
}
