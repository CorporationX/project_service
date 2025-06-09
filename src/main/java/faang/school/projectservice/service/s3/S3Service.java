package faang.school.projectservice.service.s3;

import java.io.File;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(File file, String contentType, String folder) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getName());
        log.info("==== file name {}", key);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, 
                key, 
                file
            );
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new RuntimeException("Error uploading file to S3");
        }
        return key;
    }

    public InputStream downloadFile(String objectKey) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from S3");
        }
    }

    public void listContent() {
        s3Client.listObjects(bucketName).getObjectSummaries().forEach(objectSummary -> {
            log.info("==== key {}", objectSummary.getKey());
        });
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

}
