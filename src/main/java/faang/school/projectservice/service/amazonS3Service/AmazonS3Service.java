package faang.school.projectservice.service.amazonS3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamRole;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource addResource(MultipartFile file, String key) {
        createBucket();
        checkFileIsNotEmpty(file);
        uploadFileInCloud(file, key);

        log.info("generate new resource");
        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(TeamRole.getAll());
        resource.setAllowedRoles(List.of(
            TeamRole.ANALYST,
            TeamRole.DESIGNER,
            TeamRole.DEVELOPER,
            TeamRole.INTERN,
            TeamRole.MANAGER,
            TeamRole.OWNER,
            TeamRole.TESTER));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resource;
    }

    public void updateResource(MultipartFile file, String key) {
        completeRemoval(key);
        uploadFileInCloud(file, key);
    }

    public void completeRemoval(String key) {
        log.info("calling amazonS3 deleteObject method with bucketName and key");
        amazonS3.deleteObject(bucketName, key);
    }

    private void createBucket() {
        boolean foundBucket = amazonS3.doesBucketExistV2(bucketName);

        log.info("Check exist bucket {}", bucketName);
        if (!foundBucket) {
            amazonS3.createBucket(bucketName);
        }
    }

    private void checkFileIsNotEmpty(MultipartFile file) {
        log.info("Check file");
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Image must have name");
        }
    }

    private void uploadFileInCloud(MultipartFile multipartFile, String key) {
        log.info("upload file in minio " + multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        try {
            PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, key, multipartFile.getInputStream(),
                    objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException("Image not upload" + e.getMessage());
        }
    }
}
