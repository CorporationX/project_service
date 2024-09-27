package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void upload(MultipartFile file, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            client.putObject(putObjectRequest);
        } catch (IOException exception) {
            log.error("Error upload file to S3 bucket {} message={}", bucketName, exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public S3Object download(String key) {
        try {
            return client.getObject(bucketName, key);
        } catch (Exception exception) {
            log.error("Error download file from S3 bucket {} message={}", bucketName, exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public void delete(String key) {
        try {
            client.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception exception) {
            log.error("Error remove file from S3 bucket {} key {} message={}", bucketName, key, exception.getMessage());
        }
    }
}
