package faang.school.projectservice.service.s3;

import java.io.File;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(File file, String contentType, String folder) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getName());
        PutObjectRequest putObjectRequest = new PutObjectRequest(
            bucketName, 
            key, 
            file
        );
        s3Client.putObject(putObjectRequest);
        log.debug("File is saved with name {}", key);
        return key;
    }

    public InputStream downloadFile(String objectKey) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Error dowloading file from S3, {}", e.getMessage());
            throw new RuntimeException("Error downloading file from S3");
        }
    }

    public String getAvatarContentType(String objectKey) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            return s3Object.getObjectMetadata().getContentType();
        } catch (Exception e) {
            log.error("Error getting file content type from S3, {}", e.getMessage());
            throw new RuntimeException("Error getting file content type from S3");
        }
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

}
