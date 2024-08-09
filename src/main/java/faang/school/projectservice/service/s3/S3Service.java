package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Data
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.endpoint}")
    private String endpointUrl;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String key = String.format("%s-%s", UUID.randomUUID(), fileName);

        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception exception) {
            log.error("Failed to upload file to S3. File name: {}, Key: {}, Size: {}, ContentType: {}. Error: {}",
                    fileName, key, fileSize, file.getContentType(), exception.getMessage(), exception);
            throw exception;
        }

        return key;
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    public boolean isObjectExist(String key) {
        return s3Client.doesObjectExist(bucketName, key);
    }

    public String getFileUrl(String key) {
        return String.format("%s/%s/%s", endpointUrl, bucketName, key);
    }
}
