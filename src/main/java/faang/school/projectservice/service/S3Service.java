package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void putFileInStore(String key, InputStream stream, ObjectMetadata metadata) {
        try {
            s3Client.putObject(bucketName, key, stream, metadata);
        } catch (Exception e) {
            log.error("Ошибка при сохранении файла в S3", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void putFileInStore(String key, InputStream stream) {
        putFileInStore(key, stream, null);
    }
}
