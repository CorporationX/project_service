package faang.school.projectservice.s3;

import static faang.school.projectservice.contants.InfoMessage.INFO_UPLOAD_FILE_SUCCESSFUL;
import static faang.school.projectservice.contants.InfoMessage.INFO_DELETE_FILE_SUCCESSFUL;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.contants.ErrorMessage;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @PostConstruct
    public void init() {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            log.info("S3 bucket '{}' not found. Creating...", bucketName);
            s3Client.createBucket(bucketName);
        } else {
            log.info("S3 bucket '{}' already exists.", bucketName);
        }
    }

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentLength(fileSize);
        objectMetaData.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetaData);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(ErrorMessage.ERROR_FILE_EXCEPTION);
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());
        log.info(INFO_UPLOAD_FILE_SUCCESSFUL, file.getOriginalFilename());
        return resource;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
        log.info(INFO_DELETE_FILE_SUCCESSFUL, key);
    }
}
