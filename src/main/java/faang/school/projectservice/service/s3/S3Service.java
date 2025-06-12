package faang.school.projectservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String sshKey = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, sshKey, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (SdkClientException | IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }

        return sshKey;
    }

    public InputStream downloadFile(String key) {
        log.info("Start downloading file from S3 with key: {}", key);
        try {
            S3Object object = s3Client.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            log.error("Error downloading file from S3: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
    }

    public boolean deleteFile(String key) {
        log.info("Deleting file from S3 with key: {}", key);
        try {
            s3Client.deleteObject(bucketName, key);
            return true;
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
    }
}
