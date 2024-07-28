package faang.school.projectservice.amazon;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 s3client = S3ClientProvider.getS3Client();

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource createFile(MultipartFile file, String nameFolder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = nameFolder + "/" + file.getName() + file.getSize() + file.getContentType();
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3client.putObject(putObjectRequest);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new IllegalStateException("Failed to upload file to S3");
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setName(file.getOriginalFilename());

        return resource;
    }

    public void deleteFile(Resource resource) {
        try {
            s3client.deleteObject(bucketName, resource.getKey());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new IllegalStateException("Failed to delete file from S3");
        }

        resource.setStatus(ResourceStatus.DELETED);
        resource.setKey("");
        resource.setSize(new BigInteger("0"));
    }
}
