package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3client;
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataValidationException("Failed to upload file to S3");
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());

        return resource;
    }
}
