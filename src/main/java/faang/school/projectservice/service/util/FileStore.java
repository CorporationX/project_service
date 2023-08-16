package faang.school.projectservice.service.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileStore {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void uploadFile(MultipartFile file, String key) {
        ByteArrayInputStream content;
        try {
            content = new ByteArrayInputStream(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        createBucket(bucketName);

        amazonS3.putObject(bucketName, key, content, metadata);
    }

    public void deleteFile(String keyName) {
        amazonS3.deleteObject(bucketName, keyName);
    }

    private void createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
        }
    }
}
