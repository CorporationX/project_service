package faang.school.projectservice.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
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
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    @Value("${project.gallery.image.link_expiration_time}")
    private long imageLinkExpirationTimeMillis;

    public Resource uploadFile(MultipartFile file, String folderName) {
        log.info("Uploading file {}", file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String key = String.format("%s/%s/%s", folderName, LocalDateTime.now(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                    key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new FileException("Failed to upload file", e);
        }

        log.info("File {} uploaded successfully", file.getOriginalFilename());

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resource;
    }

    public void deleteFile(String key) {
        log.info("Deleting file {}", key);
        amazonS3.deleteObject(bucketName, key);
        log.info("File {} deleted successfully", key);
    }

    public String getFileUrl(String key) {
        try {
            log.info("Getting file {} url", key);
            Date expiration = new Date(System.currentTimeMillis() + imageLinkExpirationTimeMillis);

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName,  key)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL signedUrl = amazonS3.generatePresignedUrl(request);
            log.info("File {} url retrieved successfully", key);
            return signedUrl.toString();
        } catch (Exception e) {
            log.error("Failed to download file", e);
            throw new FileException("Failed to download file", e);
        }
    }
}
