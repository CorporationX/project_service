package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.dto.image.FileData;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements ImageCloudService {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public FileData getObjectByKey(String key) {
        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, key));
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {

            byte[] bytes = inputStream.readAllBytes();
            String contentType = s3Object.getObjectMetadata().getContentType();

            return new FileData(bytes, contentType);

        } catch (Exception e) {
            logError("fetching", key, e);
        }

        return null;
    }

    @Override
    public String uploadObject(String fileName, String contentType, int fileSize, InputStream stream) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);
        String uniqueFileKey = generateUniqueFileKey(fileName);

        try {
            amazonS3.putObject(bucketName, uniqueFileKey, stream, metadata);
            return uniqueFileKey;
        } catch (Exception e) {
            logError("uploading", uniqueFileKey, e);
            return null;
        }
    }

    @Override
    public void removeObjectByKey(String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (Exception e) {
            logError("removing", key, e);
        }
    }

    private void logError(String action, String key, Exception e) {
        log.error("Error {} object with key {}: {}", action, key, e.getMessage(), e);
    }

    private String generateUniqueFileKey(String fileName) {
        return System.currentTimeMillis() + "_" + fileName;
    }
}
