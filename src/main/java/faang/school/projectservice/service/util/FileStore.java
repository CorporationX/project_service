package faang.school.projectservice.service.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.exception.FileParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStore {

    private final AmazonS3 clientAmazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void uploadFile(MultipartFile file, String key) {
        ByteArrayInputStream content;
        try {
            content = new ByteArrayInputStream(file.getBytes());
        } catch (IOException e) {
            log.error("error IOException");
            throw new FileParseException("error IOException");
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        createBucket(bucketName);

        clientAmazonS3.putObject(bucketName, key, content, metadata);
    }

    public void deleteFile(String keyName) {
        clientAmazonS3.deleteObject(bucketName, keyName);
    }

    private void createBucket(String bucketName) {
        if (!clientAmazonS3.doesBucketExistV2(bucketName)) {
            clientAmazonS3.createBucket(bucketName);
        }
    }
}
